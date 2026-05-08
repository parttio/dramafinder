import OpenAI from "openai";
import { readFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = dirname(fileURLToPath(import.meta.url));
const SKILL_PATH = resolve(__dirname, "../../SKILL.md");

const BASE_SYSTEM =
  "You are a senior engineer helping to write Playwright tests for a Vaadin application called Drama Finder. Reply with the requested code only, no commentary.";

type Provider = "local" | "gemini";

interface ProviderConfig {
  client: OpenAI;
  model: string;
  provider: Provider;
}

function getProvider(): ProviderConfig {
  const provider = (process.env.EVAL_PROVIDER ?? "local") as Provider;

  if (provider === "gemini") {
    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey) throw new Error("GEMINI_API_KEY is not set");
    return {
      provider,
      model: process.env.GEMINI_MODEL ?? "gemini-2.0-flash",
      client: new OpenAI({
        apiKey,
        baseURL:
          process.env.GEMINI_BASE_URL ??
          "https://generativelanguage.googleapis.com/v1beta/openai/",
      }),
    };
  }

  if (provider === "local") {
    return {
      provider,
      model: process.env.LOCAL_MODEL ?? "openai/gpt-oss-20b",
      client: new OpenAI({
        apiKey: "lm-studio",
        baseURL: process.env.LOCAL_BASE_URL ?? "http://127.0.0.1:1234/v1",
      }),
    };
  }

  throw new Error(`Unknown EVAL_PROVIDER: ${provider}`);
}

function buildSystem(withSkill: boolean): string {
  if (!withSkill) return BASE_SYSTEM;
  const skill = readFileSync(SKILL_PATH, "utf8");
  return `${BASE_SYSTEM}\n\nFollow the guidance in the skill below.\n\n<skill>\n${skill}\n</skill>`;
}

export interface RunResult {
  text: string;
  model: string;
  provider: Provider;
  usage: {
    prompt_tokens: number;
    completion_tokens: number;
    total_tokens: number;
  };
}

export async function runWithSkill(
  prompt: string,
  withSkill: boolean,
): Promise<RunResult> {
  const { client, model, provider } = getProvider();

  const completion = await client.chat.completions.create({
    model,
    max_tokens: 4096,
    messages: [
      { role: "system", content: buildSystem(withSkill) },
      { role: "user", content: prompt },
    ],
  });

  const text = completion.choices[0]?.message?.content ?? "";
  const usage = completion.usage;

  return {
    text,
    model,
    provider,
    usage: {
      prompt_tokens: usage?.prompt_tokens ?? 0,
      completion_tokens: usage?.completion_tokens ?? 0,
      total_tokens: usage?.total_tokens ?? 0,
    },
  };
}
