# Catalago_de_livros
Gerenciador de acervo de livros pessoal com cadastro de obras, autores, status de leitura, notas e links. Feito em Java Swing e MongoDB.
NOME: Catálogo de Livros Pessoal
Descrição:
Este projeto é um "Catálogo de Livros Pessoal" desenvolvido em Java com interface gráfica Swing e persistência de dados utilizando MongoDB. O sistema permite ao usuário gerenciar um acervo pessoal de livros e autores, registrando informações detalhadas sobre cada obra.

Tecnologias Utilizadas:

Java
Swing (para a Interface Gráfica)
MongoDB (para o Banco de Dados NoSQL)
MongoDB Java Driver
Principais Funcionalidades:

Gerenciamento de Livros:
Cadastro, alteração, exclusão e visualização de livros.
Campos: Título, Ano de Publicação (intervalo 1000-2025), Gênero (lista predefinida), Link Ilustrativo para Conteúdo, Status de Lido (Sim/Não) e Nota (0-10).
Associação de um ou mais autores a cada livro.
Gerenciamento de Autores:
Cadastro, alteração, exclusão e visualização de autores.
Campos: Nome e Ano de Nascimento.
Consultas e Filtros:
Filtragem avançada de livros por: Título, Autor(es), Gênero e Intervalo de Ano de Publicação.
Listagem e filtragem de autores por: Nome e Intervalo de Ano de Nascimento.
Interface Gráfica:
Janela principal com tabela para listagem de livros e painéis de botões para todas as operações.
Diálogos dedicados para adição/edição e filtragem de livros e autores.
Interface intuitiva com feedback ao usuário.
Objetivo:
O software foi desenvolvido como uma ferramenta prática para catalogação e organização de coleções de livros pessoais, sendo também um projeto demonstrativo de desenvolvimento de aplicações desktop com integração a banco de dados NoSQL.
