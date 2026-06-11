---
name: Finalize Issue
description: Commits changes, pushes the branch, and creates a GitHub PR.
---

# Finalize Issue

This skill automates the process of wrapping up work on an issue by committing changes, pushing the branch, and creating a pull request.

## Usage

When asked to "finalize issue", Gemini will:
1. **Identify the Issue Number**: Extract the issue number from the current branch name. The branch is expected to follow the format `<issue_number>-<slug>`.
2. **Stage and Commit**:
    - Stage all changes (`git add .`).
    - Generate a concise commit message following the format: `[xx] description`, where `xx` is the issue number.
    - Ensure the total length of the commit message is around or less than 50 characters.
3. **Push Changes**: Push the current branch to the remote repository (`origin`).
4. **Create Pull Request**: Use the GitHub CLI to create a pull request: `gh pr create -f`.

## Commands

### Get Current Branch
```bash
git branch --show-current
```

### Commit and Push
```bash
git add .
git commit -m "[<issue_number>] <concise_description>"
git push -u origin <branch_name>
```

### Create Pull Request
```bash
gh pr create -f
```
