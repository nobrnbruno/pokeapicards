# 🎴 Pokemon Cards Favorites API

Uma API REST desenvolvida em **Spring Boot** que permite buscar cartas Pokémon e gerenciar uma lista personalizada de cartas favoritas.

## 📋 Funcionalidades

- **🔍 Busca de Cartas**: Pesquise cartas Pokémon por nome utilizando a base de dados oficial do Pokemon TCG
- **⭐ Gerenciamento de Favoritos**: Adicione e remova cartas da sua lista de favoritos pessoal
- **🖼️ Integração Completa**: Busca automática de informações detalhadas das cartas, incluindo imagens
- **🌐 Interface Web**: Frontend HTML/CSS/JavaScript incluído para interação fácil com a API

## 🚀 Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring WebFlux** (Programação Reativa)
- **Maven** (Gerenciamento de dependências)
- **Pokemon TCG API** (Fonte de dados externa)

## 📦 Estrutura do Projeto

```
src/main/java/com/example/pokeapicards/
├── PokeapicardsApplication.java    # Classe principal
├── Card.java                       # Modelo de carta Pokémon
├── FavoriteCard.java              # Modelo de carta favorita
├── FavoriteCardRepository.java    # Repositório em memória
├── CardService.java               # Lógica de negócios
└── CardController.java            # Controlador REST
```

## ⚙️ Configuração e Execução

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+

### Instalação
1. Clone o repositório:
```bash
git clone <url-do-repositorio>
cd pokeapicards
```

2. Instale as dependências:
```bash
mvn clean install
```

3. Execute a aplicação:
```bash
mvn spring-boot:run
```

4. A API estará disponível em: `http://localhost:8080`

## 🌐 Interface Web

Acesse `http://localhost:8080/index.html` para utilizar a interface web que permite:
- Buscar cartas por nome
- Visualizar resultados com imagens
- Adicionar cartas aos favoritos
- Gerenciar lista de favoritos

## 📖 Endpoints da API

### 🔍 Buscar Cartas
```http
GET /api/cards?name={nome}
```
**Descrição**: Busca cartas Pokémon por nome  
**Parâmetros**:
- `name` (query, opcional): Nome da carta para buscar
  **Exemplo**: `/api/cards?name=Pikachu`

### ⭐ Listar Favoritos
```http
GET /api/favorites
```
**Descrição**: Retorna todas as cartas favoritas

### ➕ Adicionar Favorito
```http
POST /api/favorites/add
Content-Type: text/plain

{cardId}
```
**Descrição**: Adiciona uma carta aos favoritos  
**Body**: ID da carta (texto simples)  
**Exemplo**: `base1-25`

### ❌ Remover Favorito
```http
DELETE /api/favorites/{cardId}
```
**Descrição**: Remove uma carta dos favoritos  
**Parâmetros**:
- `cardId` (path): ID da carta a ser removida

## 📊 Modelos de Dados

### Card (Carta)
```json
{
  "id": "base1-25",
  "name": "Pikachu",
  "imageUrl": "https://images.pokemontcg.io/base1/25.png"
}
```

### FavoriteCard (Carta Favorita)
```json
{
  "cardId": "base1-25",
  "cardName": "Pikachu",
  "imageUrl": "https://images.pokemontcg.io/base1/25.png"
}
```

## 🔗 Integração Externa

A API utiliza a **Pokemon TCG API** oficial (https://pokemontcg.io) para:
- Buscar informações das cartas
- Obter imagens em alta qualidade
- Garantir dados sempre atualizados

## 💾 Armazenamento

Os favoritos são armazenados **em memória** durante a execução da aplicação. Para persistência permanente, considere integrar com:
- Banco de dados SQL (H2, PostgreSQL, MySQL)
- Banco NoSQL (MongoDB, Redis)
- Arquivos JSON locais

## 🛠️ Desenvolvimento

### Estrutura de Arquivos Importantes

- **`CardService.java`**: Contém toda a lógica de integração com a API externa e manipulação de favoritos
- **`CardController.java`**: Define os endpoints REST e gerencia as requisições HTTP
- **`FavoriteCardRepository.java`**: Implementa o armazenamento em memória dos favoritos
- **`index.html`**: Interface web completa para interação com a API

### Programação Reativa

A aplicação utiliza **Spring WebFlux** com:
- `Mono<T>`: Para operações que retornam um único resultado
- `Flux<T>`: Para operações que retornam múltiplos resultados
- `WebClient`: Para chamadas HTTP reativas à API externa

## 🐛 Tratamento de Erros

A API trata adequadamente:
- Cartas não encontradas (404)
- Cartas já adicionadas aos favoritos (400)
- Erros de comunicação com a API externa (500)
- Parâmetros inválidos ou faltantes