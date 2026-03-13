# 02 - Custom Library

This service uses internal shared libraries for common concerns:
- standard API response wrapper (`ResponseApi`)
- shared exception handling
- shared logging/utility components

Business logic in `core` stays framework-agnostic.

