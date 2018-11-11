DROP DATABASE IF EXISTS jokes;
CREATE DATABASE  IF NOT EXISTS jokes;
use jokes;

DROP TABLE IF EXISTS single_word;
CREATE TABLE single_word (
  word_id int PRIMARY KEY auto_increment,
  word_name varchar(50) DEFAULT NULL
) ;

DROP TABLE IF EXISTS unigraph;
CREATE TABLE unigraph (
	curr_word_id int NOT NULL,
    next_word_id int NOT NULL,
    prob double NOT NULL,
    constraint fk_curr_word_uni FOREIGN KEY (curr_word_id) REFERENCES single_word (word_id),
    constraint fk_next_word_uni FOREIGN KEY (next_word_id) REFERENCES single_word (word_id)
);

DROP TABLE IF EXISTS double_word;
CREATE TABLE double_word (
  word_id int PRIMARY KEY auto_increment,
  first_word_name varchar(50) DEFAULT NULL,
  second_word_name varchar(50) DEFAULT NULL
) ;

DROP TABLE IF EXISTS bigraph;
CREATE TABLE bigraph (
	curr_word_id int NOT NULL,
    next_word_id int NOT NULL,
    prob double NOT NULL,
    constraint fk_curr_word_bi FOREIGN KEY (curr_word_id) REFERENCES double_word (word_id),
    constraint fk_next_word_bi FOREIGN KEY (next_word_id) REFERENCES single_word (word_id)
);

 

