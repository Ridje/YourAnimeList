# YourAnimeList
Unofficial client for myanimelist.net.
It supports offline work without an account, synchronization, search and so on.

### Download apk:

<a href='https://play.google.com/store/apps/details?id=com.kis.youranimelist'>
<img src='https://user-images.githubusercontent.com/4568712/192727191-41843d46-7271-42a3-948b-d263bbf32543.png' width = '100'/>
</a>

<a href ='https://github.com/Ridje/YourAnimeList/releases/latest'>
<img src='https://user-images.githubusercontent.com/4568712/192493576-0045c0b6-cc94-4d89-b025-81492755870e.png' width = '100'/>
</a>

<a href ='https://appgallery.huawei.com/app/C107116369'>
<img src='https://user-images.githubusercontent.com/4568712/192492799-65d2c66b-aa7d-453d-bb63-35b031fded88.png' width = '100'/>
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
11. Onboarding screen.
12. Unauthorized work.

### Roadmap:
1. Animatable login screen.
2. Share anime and images, save images to gallery.
3. Navigation history.
4. Ranking lists database caching.
5. Persistence sorting in personal anime list.
6. Endless lists filtered by genre.
