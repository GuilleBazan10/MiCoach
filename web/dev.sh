#!/usr/bin/env bash
export PATH="$HOME/.nvm/versions/node/v24.12.0/bin:$PATH"
cd "$(dirname "$0")"
exec npm run dev -- --host 127.0.0.1
