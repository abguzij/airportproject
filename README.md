# Airport Management System
## Project objectives
Develop an airport management system. Add authentication with JSON Web Token. Deploy the database in a Docker container. Document the web service API using Swagger.

### System users:
- System administrator.
- Airport manager.
- Chief dispatcher.
- Dispatcher.
- Chief Engineer.
- Engineer.
- Pilot.
- Chief steward.
- Steward.
- Client.

### User actions by role
System Administrator:
1. View and add user roles.
2. Adding new employees.

Airport manager:
1. View all available data in the system.
2. Adding, changing and dismissing an employee.
3. Formation of reports.

Chief Dispatcher:
1. View available flights.
2. New flight confirmation.
3. View all registered aircraft.
4. Confirmation of new aircraft registration.
5. Flight departure confirmation.
6. Flight acceptance confirmation.

Dispatcher:
1. View available flights.
2. Creation of a new flight.
3. View all registered aircraft.
4. Registration of a new aircraft.
5. Departure of the flight.
6. Acceptance of the flight.

Chief Engineer:
1. View available aircraft.
2. View the history of the technical inspection of the aircraft.
3. Confirmation of aircraft serviceability.
4. Appointment of aircraft repair.
5. Confirmation of technical inspection of the aircraft.

Engineer:
1. View available aircraft.
2. View aircraft sent for repairs.
3. View new aircraft.
4. Drawing up a technical inspection of the aircraft.
5. Aircraft refueling.

Pilot:
1. View flight schedule.
2. View customer feedback on the flight.
3. Start of the flight.
4. Confirmation of readiness for the flight.

Chief Steward:
1. Confirmation of readiness of clients for the flight.
2. Appointment of the briefing.
3. Appointment of food distribution.
4. Confirmation of readiness for the flight.

Steward:
1. Checking the client for readiness for the flight.
2. Conducting briefing.
3. Distribution of food.
4. Confirmation of readiness for the flight.

Client:
1. View clients past flights.
2. View available flights.
3. View current flight.
4. Check-in for the flight.
5. Cancellation of check-in for a flight.
## Задачи проекта
Разработать систему по управлению деятельностью аэропорта. Добавить возможность аутентификации при помощи JSON Web Token. БД развернуть в Docker-контейнере. API веб-сервиса задокументировать при помощи Swagger. 

### Пользователи системы:
- Администратор системы.
- Управляющий аэропортом.
- Главный диспетчер.
- Диспетчер.
- Главный инженер.
- Инженер.
- Пилот.
- Главный стюард.
- Стюард.
- Клиент.

### Действия пользователей по ролям
Администратор системы:
1.	Просмотр и добавление ролей пользователей.
2.	Добавление новых работников.

Управляющий аэропортом:
1.	Просмотр всех доступных данных в системе.
2.	Добавление, изменение и увольнение работника.
3.	Формирование отчетов.

Главный диспетчер:
1.	Просмотр доступных рейсов.
2.	Подтверждение нового рейса.
3.	Просмотр всех зарегистрированных самолетов.
4.	Подтверждение регистрации нового самолета.
5.	Подтверждение отправки рейса.
6.	Подтверждение принятия рейса.

Диспетчер:
1.	Просмотр доступных рейсов.
2.	Создание нового рейса.
3.	Просмотр всех зарегистрированных самолетов.
4.	Регистрация нового самолета.
5.	Отправка рейса.
6.	Принятие рейса.

Главный инженер:
1.	Просмотр доступных самолетов.
2.	Просмотр истории технического осмотра самолета.
3.	Подтверждение исправности самолета.
4.	Назначение ремонта самолета.
5.	Подтверждение технического осмотра самолета.

Инженер:
1.	Просмотр доступных самолетов.
2.	Просмотр самолетов, отправленных на ремонт.
3.	Просмотр новых самолетов.
4.	Составление технического осмотра самолета.
5.	Заправка самолета.

Пилот:
1.	Просмотр графика рейсов.
2.	Просмотр отзывов клиентов по рейсу.
3.	Начало полета.
4.	Подтверждение готовности к рейсу.

Главный стюард:
1.	Подтверждение готовности клиентов к рейсу.
2.	Назначение проведения инструктажа.
3.	Назначение раздачи еды.
4.	Подтверждение готовности к рейсу.

Стюард:
1.	Проверка клиента на готовность к рейсу.
2.	Проведение инструктажа.
3.	Раздача еды.
4.	Подтверждение готовности к рейсу.

Клиент:
1.	Просмотр своих прошлых рейсов.
2.	Просмотр доступных рейсов.
3.	Просмотр текущего рейса.
4.	Регистрация на рейс.
5.	Отмена регистрации на рейс.


## Tools and technologies
- Java 11
- Spring Boot
- Spring Data
- PostgreSQL
- SQL
- Hibernate
- Flyway
- Spring Security
- JUnit 
- Mockito
- H2
- Maven
- Docker
- Swagger

