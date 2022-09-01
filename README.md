# YourAnimeList
Client for myanimelist.net

### Used technologies:
- Language: Kotlin
- Architecture: MVI
- Network: OkHttp, Retrofit, Coil
- Async: Coroutines, Flow
- Local data: cryptoSharedPreferences, MemoryCache, Room
- Api: https://myanimelist.net/apiconfig/references/api/v2
- DI: Hilt
- Presentation Layer: Compose

### Implemented features:
1. Login, access token, refresh access token, database initialization.
2. Explore screen.
3. Detailed item list.
4. Ranking lists, in-memory rank caching.
5. Personal anime list, offline-first work for personal list.
6. Profile.
7. Search with remote api.
