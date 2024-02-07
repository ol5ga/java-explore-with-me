# java-explore-with-me
#### Микросервисное приложение-афиша мероприятий

Предназначено для размещения мероприятий и поиска компаний для участия в них. 

### Основная функциональность:  
* Регистрация пользователя
* Просмотр мероприятия  
* Добавление нового мероприятия  
* Регистрация на имеющиеся мероприятия   
* Модерация мероприятий админом (одобрение/отклонение)  
* Добавление категории для мероприятий  
* Просмотр подборок мероприятий  

### Приложение содержит два микросервиса:

* **main-service** для бизнес-логики
* **stats-service** для сбора статистики просмотра событий по ip, который состоит из трех модулей

### Примеры запросов/Эндпоинты:  
**main-service:**  
+ POST /users/{userId}/events - добавить новое событие  
+ GET /users/{userId}/events/{eventId} - получить событие  
+ PATCH /users/{userId}/events/{eventId} - изменить событие  
+ GET /users/{userId}/events - получить события пользователя  
+ GET /users/{userId}/events/{eventId}/requests - получить запросы пользователя на участие в событии  
+ PATCH /users/{userId}/events/{eventId}/requests - изменить статус (подтверждение, отмена) заявок на участие пользователя в событии  

+ GET /categories - получить все категории  
+ GET /categories/{catId} - получить категорию  

+ GET /compilations - получить все подборки событий  
+ GET /compilations/{compId} - получить подборку событий  

+ GET /admin/events - получить события по любым параметрам:  
        users - список id пользователей  
        states - список статусов события (PENDING, PUBLISHED, CANCELED)  
        categories - список id категорий событий  
        rangeStart - начало временного отрезка в формате yyyy-MM-dd HH:mm:ss  
        rangeEnd - конец временного отрезка в формате yyyy-MM-dd HH:mm:ss  
        from - параметр для пагинации  
        size - параметр для пагинации  
+ PATCH /admin/events/{eventId} - изменить событие  

+ GET /events - получить события по любым параметрам:  
        text - текст для поиска в названии и описании событий  
        categories - список id категорий событий  
        paid - только платные события (true/false)  
        rangeStart - начало временного отрезка в формате yyyy-MM-dd HH:mm:ss  
        rangeEnd - конец временного отрезка в формате yyyy-MM-dd HH:mm:ss  
        onlyAvailable - только доступные события, т.е. у которых еще не исчерпан лимит участников (true/false)  
        sort - способ сортировки событий (EVENT_DATE, VIEWS)  
        from - параметр для пагинации  
        size - параметр для пагинации  
+ GET /events/{id} - получить событие  

+ POST /users/{userId}/requests - добавить запрос на участие в событии  
+ GET /users/{userId}/requests - получить запросы пользователя на участие в событиях  
+ DELETE /users/{userId}/requests/{requestId}/cancel - отменить запрос на участие в событии  

+ POST /users/{userId}/events/{eventId}/comments - добавить комментарий к событию  
+ PATCH /users/{userId}/events/{eventId}/comments/{commentId} - обновить комментарий  
+ GET /users/{userId}/events/{eventId}/comments/{commentId} - получить комментарий к событию  
+ DELETE /users/{userId}/events/{eventId}/comments/{commentId} - удалить комментарий к событию  
+ GET /users/{userId}/events/{eventId}/comments - получить список комментариев пользователя к событию  
+ GET /users/{userId}/comments - получить все комментарии пользователя  

+ POST /admin/users - добавить пользователя  
+ GET /admin/users - получить всех пользователей  
+ DELETE /admin/users/{userId} - удалить пользователя  
+ POST /admin/compilations - добавить подборку событий  
+ DELETE /admin/compilations/{compId} - удалить подборку событий  
+ PATCH /admin/compilations/{compId} - обновить подборку событий  
+ POST /admin/categories - добавить новую категорию  
+ GET /admin/categories/{catId} - получить категорию событий  
+ DELETE /admin/categories/{catId} - удалить категорию  
+ GET /admin/comments - получить комментрии по любым параметрам:  
        text - текст для поиска в содержании комментария  
        users - список id пользователей  
        events - список id событий  
        statuses - статусы событий (PENDING, PUBLISHED, DELETED)  
        rangeStart - начало временного отрезка в формате yyyy-MM-dd HH:mm:ss  
        rangeEnd - конец временного отрезка в формате yyyy-MM-dd HH:mm:ss  
        from - параметр для пагинации  
        size - параметр для пагинации  
        PATCH /admin/comments - изменить статусы комментариев  

**stats-service**

+ GET /stats - Получение статистики по посещениям
+ POST /hit - Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем


### Стек:  
Java, Spring Boot, Hibernate, Criteria API, PostgreSQL, Maven, Lombok, MapStruct, Junit5, Mockito, Postman, Docker
