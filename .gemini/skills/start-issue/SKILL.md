---
name: Start Issue
description: Fetches a GitHub issue, creates a branch, and proposes an implementation plan.
---

# Start Issue

This skill allows Gemini to streamline the process of starting work on a GitHub issue.

## Usage

When asked to "start issue <number>", Gemini will:
1. Use the `gh` CLI to fetch the issue title and description.
2. Create and checkout a branch named `<issue_number>-<slugified-title>` (using `create_branch.sh`).
3. Fetch the latest `main` and rebase the new branch onto it.
4. Analyze the issue content and current codebase to propose a step-by-step implementation plan.
5. **Mandatory Step**: Present the implementation plan to the user for review.
6. **Wait for Approval**: Gemini **must not** start any coding or file modifications until the user explicitly says "yes" or gives approval to the proposed plan.

## Commands

The branch creation logic is in `create_branch.sh`.
To fetch issue details:
```bash
gh issue view <issue_number> --json title,body
```
