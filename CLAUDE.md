# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# 依頼事項

Claude Code のユーザーとのやりとり、会話は必ず日本語を使用してください。

# WEB 検索

Web 検索を行う場合は、デフォルトツールではなく、Gemini CLI から検索するようにしてください。

## コマンド例

```bash
gemini -p "WebSearch: Go1.25の新機能について調べて"
```

# 技術調査

ライブラリやコード例を参照する場合は Context7 MCP を利用してください。

## プロンプト例

```txt
use context7 - Reactの最新のuseStateフックの使い方を教えて
```

# プロジェクト確認

プロジェクト固有情報に関する調査の場合は、serena MCP を使用してください。
