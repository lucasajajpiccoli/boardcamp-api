
# Boardcamp API

This API is a game rental store management system with three resources: game, customer and rental. There are seven endpoints, which permits:

- Get all games
- Create a game
- Get a costumer by id
- Create a costumer
- Get all rentals
- Create (open) a rental
- Update (close) a rental

## Setup

Before you run the project directly on your system or using the Dockerfile, you need to create the development and test PostgreSQL databases and populate the .env and .env.test files.

- 1 Create the development and test PostgreSQL databases
- 2 Populate, correspondently, .env.example and .env.test.example with databases credentials
- 3 Rename environment variables files removing ".example" at the end

Files ".env" and ".env.test" are already on ".gitignore".
## Deployment

This API is deployed on Render:

https://boardcamp-api-ee3g.onrender.com

Last update was start of February 2024 and the contracted plan was free. So, by the date the reader may be reading, the deploy may be unavailable.


## Endpoints

All exceptions or errors responded by the endpoints which are not related to the type of each attribute are an error message, i.e., a string.

Furthermore, all success responses have status code 2xx and fails 4xx.

Following the text, there are the endpoints and examples of request and reponse.

### GET /games

#### Reads all registered games in the store

##### Response

```
[
  {
    id: 1,
    name: 'Banco Imobiliário',
    image: '://',
    stockTotal: 3,
    pricePerDay: 1500
  },
  {
    id: 2,
    name: 'Detetive',
    image: '://',
    stockTotal: 1,
    pricePerDay: 2500
  },
]
```

### POST /games

#### Registers a game in the store

##### Request

```
{
  name: 'Banco Imobiliário',
  image: '://www.imagem.com.br/banco_imobiliario.jpg',
  stockTotal: 3,
  pricePerDay: 1500
}
````

##### Response

```
{
  id: 1,
  name: 'Banco Imobiliário',
  image: '://www.imagem.com.br/banco_imobiliario.jpg',
  stockTotal: 3,
  pricePerDay: 1500
}
````

##### Rules

- All attributes are mandatory
- name must be string and not empty or blank
- stockTotal and pricePerDay must be positive integers
- There cannot be already a game with the same name in the request

### GET /customers/:id

#### Reads a specific customer by id

##### Response

```
{
  id: 1,
  name: 'João Alfredo',
  cpf: '01234567890'
}
```

##### Rules

- There must be a costumer with the informed id in the system

### POST /customers

#### Registers a customer in the store

##### Request

```
{
  name: 'João Alfredo',
  cpf: '01234567890'
}
````

##### Response

```
{
  id: 1,
  name: 'João Alfredo',
  cpf: '01234567890'
}
````

##### Rules

- All attributes are mandatory
- name must be string and not empty or blank
- cpf must be a string with length 11
- There cannot be already a customer with the same cpf in the request

### GET /rentals

#### Reads all rentals, finished or not, in the store
#### Each rental presents its related game and the customer

##### Response

```
[
  {
    id: 1,
    rentDate: '2021-06-20',
    daysRented: 3,
    returnDate: null, // troca pra uma data quando já devolvido
    originalPrice: 4500,
    delayFee: 0, // troca por outro valor caso tenha devolvido com atraso
    customer: {
      id: 1,
      name: 'João Alfredo',
      cpf: '01234567890'
    },
    game: {
      id: 1,
      name: 'Banco Imobiliário',
      image: '://www.imagem.com.br/banco.jpg',
      stockTotal: 3,
      pricePerDay: 1500
    }
  }
]
```

### POST /rentals

#### Opens a rental of one unit of a game for one customer by a planned term

##### Request

```
{
  customerId: 1,
  gameId: 1,
  daysRented: 3
}
````

##### Response

```
{
  id: 1,
  rentDate: '2021-06-20',
  daysRented: 3,
  returnDate: null, 
  originalPrice: 4500,
  delayFee: 0, 
  customer: {
    id: 1,
    name: 'João Alfredo',
    cpf: '01234567890'
  },
  game: {
    id: 1,
    name: 'Banco Imobiliário',
    image: '://www.imagem.com.br/banco.jpg',
    stockTotal: 3,
    pricePerDay: 1500
  }
}
````

##### Rules

- All attributes are mandatory
- There must be game and customer informed by id in the system
- daysRented must be a positive integer
- returnDate will only be filled at the exact rental closing date, so the response is null at the rental opening
- delayFee will only be filled with the correct value at the exact rental closing date
- The rental will be created only if there are available games in the system, i.e., if the number of open rentals is smaller than the game stockTotal

### PUT /rentals/:id/return

#### Closes a rental of one unit of a game for one customer at the game return

##### Response

```
{
  id: 1,
  rentDate: '2021-06-20',
  daysRented: 3,
  returnDate: '2021-06-25', 
  originalPrice: 4500,
  delayFee: 3000, 
  customer: {
    id: 1,
    name: 'João Alfredo',
    cpf: '01234567890'
  },
  game: {
    id: 1,
    name: 'Banco Imobiliário',
    image: '://www.imagem.com.br/banco.jpg',
    stockTotal: 3,
    pricePerDay: 1500
  }
}
```

##### Rules

- There must be a rental informed by id in the system
- The rental informed by id must be open
- returnDate is now filled with the game return date
- delayFee is filled with the product of delay and game pricePerDay
- Delay is the number of days besides the planned term
