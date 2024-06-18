   drop table if exists refresh_tokens
Hibernate: 
    drop table if exists refresh_tokens_seq
Hibernate: 
    drop table if exists symbol
Hibernate: 
    drop table if exists symbol_status
Hibernate: 
    drop table if exists user
Hibernate: 
    drop table if exists user_info
Hibernate: 
    drop table if exists user_info_seq
Hibernate: 
    drop table if exists watch_symbol
Hibernate: 
    create table refresh_tokens (
        revoked bit,
        id bigint not null,
        user_id bigint,
        refresh_token varchar(10000) not null,
        primary key (id)
    ) engine=InnoDB
Hibernate: 
    create table refresh_tokens_seq (
        next_val bigint
    ) engine=InnoDB
Hibernate: 
    insert into refresh_tokens_seq values ( 1 )
Hibernate: 
    create table symbol (
        symbol varchar(255) not null,
        primary key (symbol)
    ) engine=InnoDB
Hibernate: 
    create table symbol_status (
        allowed_buy_price decimal(38,2),
        allowed_buy_yield decimal(38,2),
        best_buy_price decimal(38,2),
        current_price decimal(38,2),
        current_yield decimal(38,2),
        lower_yield decimal(38,2),
        quoterly_dividend_amount decimal(38,2),
        sell_point_yield decimal(38,2),
        upper_yield decimal(38,2),
        updated_on datetime(6),
        recommended_action varchar(255),
        symbol varchar(255) not null,
        primary key (symbol)
    ) engine=InnoDB
Hibernate: 
    create table user (
        created_on datetime(6),
        id bigint not null auto_increment,
        updated_on datetime(6),
        email varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        password varchar(255),
        user_name varchar(255),
        primary key (id)
    ) engine=InnoDB
Hibernate: 
    create table user_info (
        id bigint not null,
        email_id varchar(255) not null,
        mobile_number varchar(255),
        password varchar(255) not null,
        roles varchar(255) not null,
        user_name varchar(255),
        primary key (id)
    ) engine=InnoDB
Hibernate: 
    create table user_info_seq (
        next_val bigint
    ) engine=InnoDB
Hibernate: 
    insert into user_info_seq values ( 1 )
Hibernate: 
    create table watch_symbol (
        lower_yield decimal(38,2),
        quoterly_dividend_amount decimal(38,2),
        upper_yield decimal(38,2),
        updated_on datetime(6),
        symbol varchar(10) not null,
        primary key (symbol)
    ) engine=InnoDB
Hibernate: 
    alter table user_info 
       add constraint UKeo44j61iq2l3i834bgn193qxr unique (email_id)
Hibernate: 
    alter table refresh_tokens 
       add constraint FKnpiq3a870qyx0ilrx2gvfuiee 
       foreign key (user_id) 
       references user_info (id)