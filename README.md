# Cryptocurrency Wallet Manager :moneybag: :money_with_wings:
 
Напишете клиент-сървър приложение, което симулира личен портфейл за криптовалути. Криптовалутите са един от най-популярните варианти за инвестиране в момента. Интерес към тях имат както опитни инвеститори, така и любители. 

## Условие

Създайте клиентско конзолно приложение, което приема потребителски команди, изпраща ги за обработка на сървъра, приема отговора му и го предоставя на потребителя в human-readable формат.

*Note*: Командите и output-ът в условието са примерни, свободни сте да ги преименувате и форматирате. Единственото условие е, те да бъдат интуитивни. За улеснение на потребителя, може да имплементирате команда help.

## Функционални изисквания

- **register** - регистрация на потребител с username и password. Регистрираните потребители се пазят във файл на сървъра - той служи като база от данни, като паролата не трябва да се пази в plain text. При спиране и повторно пускане, сървърът може да зареди в паметта си вече регистрираните потребители.

- **login** - потребителят влиза в системата

- **deposit-money** - добавя определена сума пари към портфейла на потребителя. Тъй като API-то, което ще използваме в задачата, работи с долари, ще приемем, че сумите, които депозираме, ще са в долари.

    ```bash
    $ deposit-money 10000.00
    ```
- **list-offerings** - предоставя информацията за всички налични криптовалути, от които потребителят може да купува. Информацията за наличните валути взимаме от [CoinAPI](#CoinAPI)-то.
    ```bash
    $ list-offerings
    ```
- **buy** - купува количество от дадена криптовалута за определената сума пари. Сумата трябва да е налична в портфейла на потребителя.
    ```bash
    $ buy --offering=<offering_code> --money=<amount>
    ```
- **sell** - продава конкрента криптовалута. Сумата, получена от печалбата, остава в портфейла на потребителя.
    ```bash
    $ sell --offering=<offering_code>
    ```
- **get-wallet-summary** - предоставя цялостна информацията за портфейла на потребителя - информация за всички активни инвестиции към момента на изпълнение на командата, за парите в портфейла му.
    ```bash
    $ get-wallet-summary
    ```
- **get-wallet-overall-summary** - предоставя информацията за цялостната печалба/загуба от инвестициите на потребителя. Приложението сравнява цената за всяка криптовалута от момента на купуване и текущата ѝ цена, за да получи цялостната информация.
    ```bash
    $ get-wallet-overall-summary
    ```
## CoinAPI 
Информацията за криптовалутите, от която сървърът има нужда, е достъпна чрез публично безплатно REST API - [CoinAPI](https://www.coinapi.io/).

Заявките към REST API-то изискват автентикация с API key, какъвто може да получите като се регистрирате [тук](https://www.coinapi.io/pricing?apikey).

*Note*: Функционалността, която предлага  API-то, е доста обширна, но ние ще се фокусираме на описаната [тук](https://docs.coinapi.io/#list-all-assets). Тя би била достатъчна за функционалността на проекта. 

Следните endpoints от API-то биха ви били полезни:
- **GET /v1/assets** - връща инфомрация за всички достъпи asset-и в JSON формат
- **GET /v1/assets/{asset_id}** - връща инфромация за конкретен asset в JSON формат.

 - **Пример:**
```bash
 GET /v1/assets/BTC
```

```bash
[
  {
    "asset_id": "BTC",
    "name": "Bitcoin",
    "type_is_crypto": 1,
    "data_start": "2010-07-17",
    "data_end": "2021-01-24",
    "data_quote_start": "2014-02-24T17:43:05.0000000Z",
    "data_quote_end": "2021-01-24T19:07:51.7954142Z",
    "data_orderbook_start": "2014-02-24T17:43:05.0000000Z",
    "data_orderbook_end": "2020-08-05T14:38:38.3413202Z",
    "data_trade_start": "2010-07-17T23:09:17.0000000Z",
    "data_trade_end": "2021-01-24T19:08:47.4460000Z",
    "data_symbols_count": 46840,
    "volume_1hrs_usd": 9160288508835.92,
    "volume_1day_usd": 197928243055426.88,
    "volume_1mth_usd": 11571260516151083.22,
    "price_usd": 31304.448721266051267349441838,
    "id_icon": "4caf2b16-a017-4e26-a348-2cea69c34cba"
  }
]
```

 - Сървърът трябва да кешира получената от API-то информация като тя ще е валидна само за период от 30 минути, заради постоянно променящата се цена на криптовалутите.
 - Обмислете как оптимално да структурирате заявките до API-то. 
 - Може да използвате полето ***typeiscrypto*** от отговора, за да филтрирате само asset-ите, които са криптовалути. 
 - ***offeringcode*** от клиентските команди съответства на ***asset_id***-то от CoinAPI-то.
 - Тъй като API-то връща огромен набор от валути, може да изберете да показвате на потребителя само първите 50, 100 или 150 валути от резултата или да филтрирате по някакъв друг критерий. Този избор оставяме на вас.

## Нефункционални изисквания

- Сървърът може да обслужва множество потребители паралелно.
- Сървърът пази информацията за потребителите и техните портфейли по начин, който му позволява след спиране или рестартиране да може да зареди тази информация отново. Помислете за подходящ формат на данни, в който може да съхранявате информацията от сървъра.

## Съобщения за грешки

При неправилно използване на програмата, на потребителя да се извеждат подходящи съобщения за грешка.

При възникване програмна грешка, на потребителя да се извежда само уместна за него информация. Техническа информация за самата грешка и stackтraces да се записват във файл на файловата система - няма определен формат за записване на грешката.

Например, нерелевантно е при логин на потребител и възникнал проблем с мрежовата комуникация, да се изписва грешка от вида на "IO exception occurred: connection reset", по-подходящо би било "Unable to connect to the server. Try again later or contact administrator by providing the logs in <path_to_logs_file>".

При възникване на програмна грешка от страна на сървъра, подходящо съобщение се изписва на конзолата и във файл, като освен това, във файла се записва допълнителна информация (например, при заявка на кой потребител е възникнала грешката, ако въобще е обвързана с потребителско взаимодействие) и stacktraces.

## Уточнения

- Валидацията на потребителския вход е задължителна, т.е. покрийте всички сценарии, за които се сетите с невалиден вход - било то null, грешно форматиране, невалиден тип на данните и т.н.
- Командите и output-ът от тях са примерни. Свободни сте да представите ваша интерпретация на командите, както и да добавите нови. Единствената им цел тук е да помогнат за разбирането на условието.
- Всякакви допълнителни функционалности, за които се сетите, са добре дошли.

## Submission

Качете в грейдъра `.zip` архив на познатите директории `src` и `test`. Ако пакетирате допълнителни файлове (които не са .java), те трябва да са в корена на архива, на нивото на `src` и `test`.

В грейдъра няма да има автоматизирани референтни тестове.

Проектът ви трябва да е качен в грейдъра не по-късно от 18:00 в деня преди датата на защитата.

**Успех!** 🍀
