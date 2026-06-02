---
name: Create Issue Branch
description: Gets the title of a GitHub issue and creates a new branch named <issue_number>-<slugified-title>.
---

# Create Issue Branch

This skill allows Gemini to automate the creation of git branches based on GitHub issues.

## Usage

When asked to create a branch for a specific issue number, this skill will:
1. Use the `gh` CLI to fetch the issue title.
2. Slugify the title (lowercase, replace non-alphanumeric with hyphens).
3. Create and checkout a branch with the name format `<issue_number>-<slugified-title>`.

## Commands

The core logic is implemented in `create_branch.sh` in the project root.
