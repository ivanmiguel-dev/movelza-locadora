# MOVELZA — Sistema Web de Locação de Veículos

Projeto acadêmico Full Stack desenvolvido para aplicar conhecimentos de Análise e Desenvolvimento de Sistemas, integrando interface web, regras de negócio, API e banco de dados.

## Sobre o projeto

A MOVELZA é uma aplicação demonstrativa de locação de veículos, com catálogo de 220 veículos distribuídos em 10 categorias. O sistema permite consultar informações dos automóveis, verificar disponibilidade, calcular valores de locação e solicitar reservas.

## Funcionalidades

- Catálogo de veículos organizado por categorias e modelos.
- Exibição de imagens, características e valores de diárias.
- Consulta de disponibilidade por período.
- Cálculo automático do valor estimado da locação.
- Formulário de solicitação de reservas.
- Painel administrativo para acompanhamento de veículos e reservas.
- Gerenciamento dos status pendente, confirmado e cancelado.
- Regras de negócio para evitar reservas conflitantes no mesmo período.

## Tecnologias utilizadas

**Backend:** Java, Spring Boot e APIs REST.

**Banco de dados:** MySQL.

**Frontend:** HTML5, CSS3 e JavaScript.

**Ambiente de desenvolvimento:** IntelliJ IDEA.

## Estrutura do repositório

```text
movelza-locadora/
├── backend/    # Aplicação Java com Spring Boot
├── frontend/   # Páginas, estilos, scripts e imagens
└── README.md   # Documentação do projeto
```

## Execução local

Este projeto foi desenvolvido para execução em ambiente local. Para utilizá-lo, é necessário configurar o MySQL e as propriedades de conexão do backend, iniciar a aplicação Spring Boot e acessar o frontend conforme a configuração do ambiente.

O arquivo `application.properties.example` serve como referência para a configuração local. Não publique credenciais reais no repositório.

## Aprendizados

O desenvolvimento da MOVELZA proporcionou experiência prática em integração entre frontend, backend e banco de dados, organização de código, implementação de regras de negócio e testes funcionais.

## Observação

Este é um projeto acadêmico de demonstração, desenvolvido para fins de estudo e portfólio. Não representa um serviço comercial de locação de veículos.

## Autor

Desenvolvido por [ivanmiguel-dev](https://github.com/ivanmiguel-dev).
