require: city/city.sc
    module = sys.zb-common

theme: /
    state: Greeting
        q!: $regex</start>
        a: Здравствуйте! Я ваш персональный помощник по подбору экскурсии!
        go!: /getCity
        
        
        
    state: getCity
        a: В каком городе вас интересует экскурсия?
        buttons:
            "Пропустить" -> ./skipCatchCity
            "Вернуться на шаг назад" -> /Greeting
        
        state: catchCity
            q: * [в] $City *
            script:
                $session.city = $parseTree._City.name
            a: Замечательно, вы выбрали город {{$session.city}}, давайте перейдем к следующему вопросу!
            go!: /getCategoty
        
        state: skipCatchCity
            # q: * (пропустить*/пропуск*/нет*/не хочу*) *
            a: Хорошо, вы пропустили выбор города для экскурсии, давайте перейдем к следующему вопросу!
            script:
                $session.city = null;
            go!: /getCategoty
        
        state: сatchCityError
            event: noMatch
            a: Я не знаю такого города. Давайте вы назовете его еще раз!?
            go!: /getCity
            
        
        # state: catchCity12
        #     q: * [в] $City * * @duckling.number * * @duckling.number *
        #     script:
        #         $session.city = $parseTree._City.name
        #         $session.budget = $parseTree["_duckling.number"];
        #         $session.max_participants = $parseTree["_duckling.number"];
        #     a: Замечательно, вы выбрали город {{$session.city}}, давайте перейдем к следующему вопросу!
        #     a: Хорошо! Ваш бюджет {{$session.budget}}!
        #     a: Отлично! Максимально допустимое количество участников {{$session.max_participants}}.
        #     go!: /getBudget
        
    state: getCategoty
        a: Какой вид экскурсий предпочитаете? Зимние, культурные, природные или другие?
        buttons:
            "Пропустить" -> ./skipСatchCategoty
            "Вернуться на шаг назад" -> /getCity
        state: catchCategory
            q: * @categories *
            a: Категория для обращения к API: {{ $parseTree._categories.name }}
            script:
                $session.category = $parseTree._categories.name
            a: Отлично! Вы выбрали категорию - {{$session.category}}.
            go!: /getBudget
        state: skipСatchCategoty
            # q: * (пропустить*/пропуск*/нет*/не хочу*) *
            a: Хорошо, вы пропустили выбор категории для экскурсии, давайте перейдем к следующему вопросу!
            script:
                $session.category = null;
            go!: /getBudget
        state: сatchCategotyError
            event: noMatch
            a: Вы ввели непонятное для меня значение. Введите пожалуйста корректную сумму!
            go!: /getCategoty    
        
        
        
    state: getBudget
        a: Каков ваш бюджет?
        buttons:
            "Пропустить" -> ./skipСatchBudget
            "Вернуться на шаг назад" -> /getCategoty
            
        state: catchBudget
            q: * @duckling.number *
            script:
                $session.budget = $parseTree["_duckling.number"];
            a: Хорошо! Ваш бюджет {{$session.budget}}!
            go!: /getParticipant
            
        state: skipСatchBudget
            # q: * (пропустить*/пропуск*/нет*/не хочу*) *
            a: Хорошо, вы пропустили выбор бюджета для экскурсии, давайте перейдем к следующему вопросу!
            script:
                $session.budget = null;
            go!: /getParticipant
            
        state: сatchBudgetError
            event: noMatch
            a: Вы ввели непонятное для меня значение. Введите пожалуйста корректную сумму!
            go!: /getBudget
    
            
            
    
    state: getParticipant
        a: Какое для вас допустимое количество участников?
        buttons:
            "Пропустить" -> ./skipCatchParticipant
            "Вернуться на шаг назад" -> /getBudget
            
        state: catchParticipant
            q: * @duckling.number *
            script:
                $session.max_participants = $parseTree["_duckling.number"];
            a: Отлично! Максимально допустимое количество участников {{$session.max_participants}}.
            go!: /Confirm
            
        state: skipCatchParticipant
            # q: * (пропустить*/пропуск*/нет*/не хочу*) *
            a: Хорошо, вы пропустили выбор количества участников в экскурсии!
            script:
                $session.max_participants = null;
            go!: /Confirm
            
        state: catchParticipantError
            event: noMatch
            a: Я не понял, что это за цифра. Введите пожалуйста корректное число участников!
            go!: /getParticipant
    
    
    
    state: Confirm
        if: $session.city == "" && $session.budget == "" && $session.max_participants == ""
            a: Вы не указали ни одного критерия поиска, так я не смогу подобрать вам экскурсию. Укажите хотя-бы город!
            go!: /getCity
        else:
            script:
                if ($session.city != null && $session.budget != null && $session.max_participants != null) {
                    $temp.answer = "Итак, вы хотите пройти экскурсию в городе " + $session.city + 
                    ", с бюджетом до " + $session.budget + " рублей и максимально допустимым количеством участников: "
                    + $session.max_participants + ". \nВсё верно?";
                } else if ($session.city != null && $session.budget != null && $session.max_participants == null) {
                    $temp.answer = "Итак, вы хотите пройти экскурсию в городе " + $session.city + 
                    ", с бюджетом до " + $session.budget + " рублей, без ограничения по количеству участников. \nВсё верно?";
                } else if ($session.city != null && $session.budget == null && $session.max_participants != null) {
                    $temp.answer = "Итак, вы хотите пройти экскурсию в городе " + $session.city + 
                    ", без указания бюджета, с максимально допустимым количеством участников: " + $session.max_participants + 
                    ". \nВсё верно?";
                } else if ($session.city == null && $session.budget != null && $session.max_participants != null) {
                    $temp.answer = "Итак, вы хотите пройти экскурсию без указания города, с бюджетом до " + $session.budget + 
                    " рублей и максимально допустимым количеством участников: " + $session.max_participants + ". \nВсё верно?";
                } else if ($session.city != null && $session.budget == null && $session.max_participants == null) {
                    $temp.answer = "Итак, вы хотите пройти экскурсию в городе " + $session.city + 
                    ", без указания бюджета и количества участников. \nВсё верно?";
                } else if ($session.city == null && $session.budget != null && $session.max_participants == null) {
                    $temp.answer = "Итак, вы хотите пройти экскурсию без указания города, с бюджетом до " + $session.budget + 
                    " рублей, без ограничения по количеству участников. \nВсё верно?";
                } else if ($session.city == null && $session.budget == null && $session.max_participants != null) {
                    $temp.answer = "Итак, вы хотите пройти экскурсию без указания города и бюджета, с максимально допустимым количеством участников: " + $session.max_participants + 
                    ". \nВсё верно?";
                }
            a: {{$temp.answer}}
            buttons:
                "Да" -> /newState
                "Вернуться в начало" -> /Greeting
            
        # СЮДА ЧТО ТО ПОЙДЕТ
        buttons:
            "Вернуться в начало" -> /Greeting
        script:
            $session.city = null;
            $session.budget = null;
            $session.max_participants = null;


    
    state: newState
        script:
            $temp.response = $http.post(
                "http://217.114.7.99/filter_excursions/", 
                {
                    body: {
                        "city": $session.city,
                        "price": $session.budget,
                        "max_participants": $session.max_participants
                    },
                    headers: {
                        "Content-Type": "application/json"
                    }
                }
            );
        # a: {{$temp.response.error}}
        if: $temp.response.isOk
            a: Сейчас в городе
            script:
                var startDate = new Date($temp.response.data.start_date);
                var endDate = new Date($temp.response.data.end_date);
            
            
                  var monthsGenitive = [
                "января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"
                    ];
            
                $temp.startDateFormatted = 
                    startDate.getDate() + " " +
                    monthsGenitive[startDate.getMonth()] + " " +
                    startDate.getFullYear() + " " +
                    startDate.getHours() + ":" + 
                    (startDate.getMinutes() < 10 ? "0" : "") + startDate.getMinutes();
                
                $temp.endDateFormatted = 
                    endDate.getHours() + ":" + 
                    (endDate.getMinutes() < 10 ? "0" : "") + endDate.getMinutes();
                $session.idEx =$temp.response.data.excursion_id
            a: Мы подобрали для вас такую экскурсию:\n\n{{$temp.response.data.excursion_name}} – {{$temp.response.data.excursion_description}}\n\nЦена: {{$temp.response.data.price}}\nКоличество человек: {{$temp.response.data.max_participants}}\n\nДата: {{$temp.startDateFormatted}} – {{$temp.endDateFormatted}}\n\nЛокация: {{$temp.response.data.location}}\n\nЭкскурсовод: {{$temp.response.data.organizer_name}}
            
            buttons:
                "Записаться на экскурсию" -> /newState/booking
                "Другой вариант🔃" -> /newState
                "В начало" -> /Greeting
        else:
            a: Я не нашел экскурсию, пожалуйста давайте подберем ее заново!
            go!: /getCity
            
        # a: Мы подобрали для вас такую экскурсию:\n\n{{$temp.response.data.excursion_name}}\n{{$temp.response.data.excursion_description}}\n\nЦена: {{$temp.response.data.price}}\nКоличество человек: {{$temp.response.data.max_participants}}\n\nНачало экскурсии {{$temp.response.data.start_date}} - конец {{$temp.response.data.end_date}}\n\nЛокация: {{$temp.response.data.location}}\n\nЭкскурсовод: {{$temp.response.data.organizer_name}}
        
            
        state: booking
            a: Как вас зовут?
            state: Name
                q!: * {[@pymorphy.surn] * [@mystem.persn] * [@mystem.patrn]}  *
                script:
                    $session.f = $parseTree["_pymorphy.surn"];
                    $session.i = $parseTree["_mystem.persn"];
                    $session.o = $parseTree["_mystem.patrn"];
                if: $session.f == undefined
                    script: $session.f = ' '
                if: $session.i == undefined
                    script: $session.i = ' '
                if: $session.o == undefined
                    script: $session.o = ' '
                script:
                    $session.fio = capitalize($session.f) + " " + capitalize($session.i) + " " + capitalize($session.o);
                    
                a: Введите ваш номер телефона: 
                    
                state: Phone
                    q: * @duckling.phone-number *
                    script:
                        $session.phone = $parseTree["_duckling.phone-number"];
                    go!: ./newState2
                    
                    state: newState2
                        a: {{$session.idEx}}
                        script:
                            $temp.response = $http.post(
                                "URL", 
                                {
                                    body: {
                                        "fio": $session.fio,
                                        "phone": $session.phone,
                                        "idEx": $session.idEx
                                    },
                                    headers: {
                                        "Content-Type": "application/json"
                                    }
                                }
                            );
                        


    state: сatchAll
        event!: noMatch
        random:
        a: Извините, я не понял.
        a: Извините, я не понимаю, что мне с этим делать.
        a: Не могли бы вы повторить?
        a: Повторите, пожалуйста.
        buttons:
            "Вернуться в начало" -> /Greeting












# require: city/city.sc
#     module = sys.zb-common

# theme: /
#     state: Greeting
#         q!: $regex</start>
#         a: Назови категорию и я скажу, какой её вариант используется для обращения к API
#         go!: /getCity

#     state: getCity
#         a: Называйте!
#         event: noMatch || toState = "./"
#         state: catchCity
#             q: * @categories *
#             a: Категория для обращения к API: {{ $parseTree._categories.name }}