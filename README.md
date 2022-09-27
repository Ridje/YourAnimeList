# YourAnimeList
Client for myanimelist.net

### Load

[![name](https://user-images.githubusercontent.com/4568712/192491048-a7fef60c-873d-4070-b022-55d06069554b.png)](https://appgallery.huawei.com/app/C107116369)

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
5. Personal anime list, offline-first work for personal list, merge synchronization.
6. Profile.
7. Search with remote api.
8. Settings and additional info screen.
9. Suggestions list.
10. Edit personal anime status screen.

### Roadmap:
1. Introdution screen.
2. Animatable login screen.
3. Register and help button on login screen.
4. Share anime and images, save images to gallery.
5. Unauthorized work.
6. Navigation history.
7. Ranking lists database caching.
8. Drop ranking cache on NSFW change and synchronize local database in case of not loaded nsfw items (add delay to enqueing work manager work in case of frequent switcher clicking).
