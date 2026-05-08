# vaadin-playwright-test — eval harness

Phase 0 bootstrap. See `../../../PLAN.md` for the full plan.

The harness uses the OpenAI SDK against any OpenAI-compatible endpoint.
Two providers are supported out of the box:

- `local` — LM Studio (default), e.g. `openai/gpt-oss-20b` at `http://127.0.0.1:1234/v1`
- `gemini` — Google's OpenAI-compatible endpoint

## Setup

```bash
cd skills/vaadin-playwright-test/evals
npm install
cp .env.example .env
# fill in keys / pick a provider
```

For self-hosted Langfuse:

```bash
docker compose up -d
# then set LANGFUSE_HOST=http://localhost:3000 in .env
```

## Run

```bash
# default: local LM Studio
npm run smoke

# Gemini
EVAL_PROVIDER=gemini npm run smoke
```

Runs one hardcoded prompt twice (with/without the skill in the system prompt)
and logs both as Langfuse traces tagged `phase=bootstrap`.

## Note on prompt caching

The PLAN.md calls for prompt caching on the system prompt. Neither LM Studio
nor Gemini's OpenAI-compatible endpoint supports the Anthropic-style
`cache_control` parameter, so caching is a no-op here. Re-introduce it when
the harness is pointed at a provider that supports it.
