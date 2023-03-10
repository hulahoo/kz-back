update tsadv_dic_absence_type
set IS_REQUIRED_ORDER_NUMBER = true
where lang_value1 not in ('Line Recruiting Day',
                          'Line Training Day',
                          'Больничный по заболеванию',
                          'Больничный по проф-заболеванию',
                          'Больничный по уходу',
                          'Внутреннее обучение',
                          'Вынужденный прогул',
                          'Выполнение государственных и общественных обязанностей',
                          'Карантин',
                          'Отгул',
                          'Прогул',
                          'Прочие справки',
                          'Справка (донор)',
                          'Справка МЦГА',
                          'Справка МЦГА (профлечение)',
                          'Удаленная работа');