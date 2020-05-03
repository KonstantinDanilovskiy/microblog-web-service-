create table message (
  id bigserial not null,
  filename varchar(255),
  tag varchar(255),
  text varchar(2048) not null,
  user_id int8 not null,
  primary key (id));
create table user_role (
  user_id int8 not null,
  roles varchar(255) not null check ( roles in ('ADMIN', 'USER', 'GUEST')));
create table usr (
  id bigserial not null,
  activation_code varchar(255),
  active boolean not null,
  email varchar(255),
  password varchar(255) not null,
  username varchar(255) not null unique,
  primary key (id));
alter table if exists message add constraint message_user_id_fk foreign key (user_id) references usr;
alter table if exists user_role add constraint usr_user_id_fk foreign key (user_id) references usr;