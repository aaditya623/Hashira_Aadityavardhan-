# Hashira_Aadityavardhan-
“Implementation of Shamir’s Secret Sharing Scheme with multi-base support for secure secret distribution and recovery.”
# Shamir's Secret Sharing (Multi-Base Support)

This repository contains an implementation of **Shamir’s Secret Sharing Scheme** with support for numbers represented in different bases. The scheme allows a secret to be split into multiple shares and later reconstructed only when a threshold number of shares is provided.

## Features
- Secure implementation of Shamir’s Secret Sharing.
- Multi-base input support (e.g., binary, decimal, base-4).
- Configurable number of total shares (`n`) and threshold (`k`).
- Easy secret reconstruction when enough valid shares are provided.

## Example
### Input
```json
{
  "keys": {
    "n": 4,
    "k": 3
  },
  "1": {
    "base": "10",
    "value": "4"
  },
  "2": {
    "base": "2",
    "value": "111"
  },
  "3": {
    "base": "10",
    "value": "12"
  },
  "6": {
    "base": "4",
    "value": "213"
  }
}
