drop table if exists sketch;

create table sketch (
	id int not null auto_increment,
	name varchar(64) default null,
	group_id int,
	primary key (id)
);


drop table if exists sketch_group;

create table sketch_group (
	id int not null auto_increment,
	name varchar(64) default null,
	group_id int,
	primary key (id)
);

drop table if exists vertex;

create table vertex (
	id int not null auto_increment,
	owner_id int,
	inx int,
	x double,
	y double,
	z double,
	primary key (id)
);

drop table if exists edge;

create table edge (
	id int not null auto_increment,
	owner_id int,
	inx int,
	from_id int,
	to_id int,
	primary key (id)
);

drop table if exists surface;

create table surface (
	id int not null auto_increment,
	owner_id int,
	inx int,
	visible bit,
	exterior_id int default null,
	primary key (id)
);

drop table if exists surface_edge;

create table surface_edge (
	inx int,
	surface_id int,
	edge_id int,
	primary key (surface_id, edge_id)
);
