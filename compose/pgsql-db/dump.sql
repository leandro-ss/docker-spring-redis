\set ON_ERROR_STOP 1
BEGIN;

-- FUNCTIONS
create or replace function padded_by_whitespace(text) returns boolean as $$
  select btrim($1) <> $1;
$$ language sql immutable;
create or replace function whitespace_collapsed(text) returns boolean as $$
  select $1 !~ e'\\s{2,}';
$$ language sql immutable;
create or replace function controlled_for_whitespace(text) returns boolean as $$
  select not padded_by_whitespace($1) and whitespace_collapsed($1);
$$ language sql immutable set search_path = musicbrainz, public;


-- TABLES
create table creator(
		id int not null,
		name varchar( 100 ) not null,
		bio text,
		birth timestamp with time zone default now (),
		death timestamp with time zone default now (),
		image_token varchar( 20 ),
    status bool default true
);
create table collectable(
		id int not null,
		id_creator  int not null,
		id_rarity int,
		name varchar( 100 ) not null,
		bio text,
		release timestamp with time zone default now (),
		image_token varchar( 20 ),
    status bool default true
);
create table collectable_item(
		id int not null,
		id_collectable  int not null,
		name varchar( 100 ) not null,
		bio text,
		release timestamp with time zone default now (),
		image_token varchar( 20 ),
        status bool default true
);

-- DOMAIN
create table rarity (id integer, descr varchar(30));
insert into rarity values ( 1, 'COMUM');
insert into rarity values ( 2, 'RARO');
insert into rarity values ( 3, 'ESGOTADO');

-- INDEX
CREATE INDEX creator_idx_name ON creator (name);
CREATE UNIQUE INDEX creator_idx_id ON creator (id);
CREATE INDEX collectable_idx_name ON collectable (name);
CREATE UNIQUE INDEX collectable_idx_id ON collectable (id);
CREATE INDEX collectable_item_idx_name ON collectable_item (name);
CREATE UNIQUE INDEX collectable_item_idx_id ON collectable_item (id);

-- CONSTRAINT
ALTER TABLE creator
  ADD CONSTRAINT control_for_whitespace CHECK (controlled_for_whitespace(name)),
  ADD CONSTRAINT only_non_empty CHECK (name != '');
ALTER TABLE collectable
  ADD CONSTRAINT control_for_whitespace CHECK (controlled_for_whitespace(name)),
  ADD CONSTRAINT only_non_empty CHECK (name != '');
ALTER TABLE collectable_item
  ADD CONSTRAINT control_for_whitespace CHECK (controlled_for_whitespace(name)),
  ADD CONSTRAINT only_non_empty CHECK (name != '');

-- PK
ALTER TABLE creator ADD CONSTRAINT creator_pkey PRIMARY KEY (id);
ALTER TABLE collectable ADD CONSTRAINT collectable_pkey PRIMARY KEY (id);
ALTER TABLE collectable_item ADD CONSTRAINT collectable_item_pkey PRIMARY KEY (id);

-- FK
ALTER TABLE collectable ADD CONSTRAINT creator_fk_collectable
   FOREIGN KEY (id_creator)
   REFERENCES creator(id);
ALTER TABLE collectable_item ADD CONSTRAINT collectable_item_fk_collectable
   FOREIGN KEY (id_collectable)
   REFERENCES collectable(id);
ALTER TABLE collectable_item ADD CONSTRAINT collectable_item_fk_rarity
   FOREIGN KEY (id_rarity)
   REFERENCES rarity(id);

CREATE SEQUENCE creator_seq START 1;	 
CREATE SEQUENCE collectable_seq START 1;	 
CREATE SEQUENCE collectable_item_seq START 1;

COMMIT;

-- vi: set ts=4 sw=4 et :
