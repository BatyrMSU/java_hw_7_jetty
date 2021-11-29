# Веб-сервер на основе jetty

Осуществляется подключение к предварительно созданной базе данных product_service. <br>
Конфигурация подключения задается в *service/JDBCCredentials* и *jooq-generated/build.gradle.kts*. <br>
Работа с записями осуществляется с помощью jooq. <br> <br>

База данных инициализируется через *service/db_init/Main*. <br>
Запуск сервера: *service/servlets/ServletMain*. <br> <br>

Логин и пароль: <br>
guest, guest <br>
manager, manager


