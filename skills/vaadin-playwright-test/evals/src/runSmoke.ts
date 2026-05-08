import { Langfuse } from "langfuse";
import { runWithSkill } from "./harness.js";

const SMOKE_PROMPT =
  "Write a Playwright Java test for the Drama Finder app that fills the TextField labeled 'Title' with 'Hamlet' and clicks the Button labeled 'Search'. Show only the test method body.";

async function main() {
  const langfuse = new Langfuse();

  for (const withSkill of [false, true]) {
    const label = withSkill ? "with-skill" : "without-skill";
    console.log(`\n=== ${label} ===`);

    const start = new Date();
    const result = await runWithSkill(SMOKE_PROMPT, withSkill);
    const end = new Date();

    const trace = langfuse.trace({
      name: `smoke-${label}`,
      tags: ["phase=bootstrap", label],
      input: SMOKE_PROMPT,
      metadata: {
        withSkill,
        provider: result.provider,
        model: result.model,
      },
    });

    trace.generation({
      name: "chat.completions.create",
      model: result.model,
      modelParameters: { provider: result.provider },
      startTime: start,
      endTime: end,
      input: [
        { role: "system", content: "<see metadata>" },
        { role: "user", content: SMOKE_PROMPT },
      ],
      output: result.text,
      usage: {
        input: result.usage.prompt_tokens,
        output: result.usage.completion_tokens,
        total: result.usage.total_tokens,
      },
      metadata: { withSkill, provider: result.provider, model: result.model },
    });

    trace.update({ output: result.text });

    console.log(result.text);
  }

  await langfuse.shutdownAsync();
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
