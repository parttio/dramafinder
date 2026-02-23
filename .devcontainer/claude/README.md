# Claude AI Agent Devcontainer

This devcontainer is purpose-built for use with AI agents (such as Claude Code). It provides a sandboxed environment with read-only GitHub access — **it does not have git write access**. Any pushes to remote repositories must be performed from your local environment.

---

## GitHub Personal Access Token (PAT)

A GitHub PAT with read access is required for the devcontainer to authenticate with GitHub (e.g. to clone private repositories or read repository data).

### Step 1: Create a PAT on GitHub

1. Go to [GitHub Settings → Developer settings → Personal access tokens → Tokens](https://github.com/settings/personal-access-tokens)
2. Click **Generate new token**
3. Give the token a descriptive name (e.g. `dramafinder-devcontainer-read`)
4. Set an expiration date as appropriate
5. Under **Repository access**, check:
   - Public repositories (if no need for other (private) repositories in your container)
6. Click **Generate token**
7. Copy the token immediately — you will not be able to see it again

---

### Step 2: Set the PAT as an Environment Variable

Set the token as `DRAMAFINDER_PAT` in your local environment so it is passed into the devcontainer automatically.

#### Linux / macOS

Add the following line to your shell profile (`~/.bashrc`, `~/.zshrc`, `~/.bash_profile`, etc.):

```bash
export DRAMAFINDER_PAT="ghp_your_token_here"
```

Then reload your shell:

```bash
source ~/.bashrc   # or ~/.zshrc, depending on your shell
```

To verify:

```bash
echo $DRAMAFINDER_PAT
```

#### Windows (Command Prompt / System Environment Variables)

**Option A - System Environment Variables:**

1. Open **Start** → search for **Environment Variables**
2. Click **Edit the system environment variables**
3. Click **Environment Variables...**
4. Under **User variables**, click **New**
5. Set **Variable name**: `DRAMAFINDER_PAT`
6. Set **Variable value**: `ghp_your_token_here`
7. Click **OK** on all dialogs
8. Restart any open terminals or VS Code for the change to take effect

**Option B - PowerShell (persistent, current user):**

```powershell
[System.Environment]::SetEnvironmentVariable("DRAMAFINDER_PAT", "ghp_your_token_here", "User")
```

---

> **Security note:** Never commit your PAT to source control. Treat it like a password.
