# YourAnimeList
Unofficial client for myanimelist.net
It supports offline work without an account, synchronization, search and so on.

### Download apk:

<a href ='https://github.com/Ridje/YourAnimeList/releases/latest'>
<img src='https://user-images.githubusercontent.com/4568712/192493576-0045c0b6-cc94-4d89-b025-81492755870e.png' width = '200'/>
</a>

<a href ='https://appgallery.huawei.com/app/C107116369'>
<img src='https://user-images.githubusercontent.com/4568712/192492799-65d2c66b-aa7d-453d-bb63-35b031fded88.png' width = '200'/>
</a>

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
