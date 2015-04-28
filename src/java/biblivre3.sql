CREATE ROLE biblivre LOGIN
  ENCRYPTED PASSWORD 'md52690de6f151b12923e0527bb496da66f'
  SUPERUSER INHERIT CREATEDB CREATEROLE;

CREATE DATABASE biblivre3 WITH OWNER = biblivre ENCODING = 'UTF8';

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;


CREATE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: access_control; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE access_control (
    serial integer NOT NULL,
    serial_card integer NOT NULL,
    serial_station integer,
    serial_reader integer NOT NULL,
    entrance_datetime timestamp without time zone NOT NULL,
    departure_datetime timestamp without time zone
);


ALTER TABLE public.access_control OWNER TO biblivre;

--
-- Name: access_control_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE access_control_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.access_control_serial_seq OWNER TO biblivre;

--
-- Name: access_control_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE access_control_serial_seq OWNED BY access_control.serial;


--
-- Name: access_control_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('access_control_serial_seq', 1, false);


--
-- Name: acquisition_item_quotation; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE acquisition_item_quotation (
    serial_requisition integer NOT NULL,
    serial_quotation integer NOT NULL,
    quotation_quantity integer,
    unit_value numeric,
    response_quantity integer
);


ALTER TABLE public.acquisition_item_quotation OWNER TO biblivre;

--
-- Name: order_serial_order_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE order_serial_order_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.order_serial_order_seq OWNER TO biblivre;

--
-- Name: order_serial_order_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('order_serial_order_seq', 1, false);


--
-- Name: acquisition_order; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE acquisition_order (
    serial_order integer DEFAULT nextval('order_serial_order_seq'::regclass) NOT NULL,
    serial_quotation integer NOT NULL,
    order_date timestamp without time zone,
    responsable character varying(50),
    obs text,
    status character(2),
    invoice_number character varying(50),
    receipt_date timestamp without time zone,
    total_value numeric,
    delivered_quantity integer,
    terms_of_payment character varying(50),
    deadline_date timestamp without time zone
);


ALTER TABLE public.acquisition_order OWNER TO biblivre;

--
-- Name: quotation_serial_quotation_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE quotation_serial_quotation_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.quotation_serial_quotation_seq OWNER TO biblivre;

--
-- Name: quotation_serial_quotation_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('quotation_serial_quotation_seq', 1, false);


--
-- Name: acquisition_quotation; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE acquisition_quotation (
    serial_quotation integer DEFAULT nextval('quotation_serial_quotation_seq'::regclass) NOT NULL,
    quotation_date timestamp without time zone,
    serial_supplier integer,
    response_date timestamp without time zone,
    expiration_date timestamp without time zone,
    delivery_time integer,
    responsable character varying(30),
    obs text
);


ALTER TABLE public.acquisition_quotation OWNER TO biblivre;

--
-- Name: requisition_serial_requisition_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE requisition_serial_requisition_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.requisition_serial_requisition_seq OWNER TO biblivre;

--
-- Name: requisition_serial_requisition_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('requisition_serial_requisition_seq', 1, false);


--
-- Name: acquisition_requisition; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE acquisition_requisition (
    serial_requisition integer DEFAULT nextval('requisition_serial_requisition_seq'::regclass) NOT NULL,
    requisition_date timestamp without time zone,
    responsable character varying(30),
    author_type character varying(10),
    author character varying(50),
    num_prename character varying(20),
    author_title character varying(50),
    item_title character varying(100),
    item_subtitle character varying(100),
    edition_number character varying(20),
    publisher character varying(50),
    obs text,
    status character varying(2),
    requester character varying(30),
    quantity integer
);


ALTER TABLE public.acquisition_requisition OWNER TO biblivre;

--
-- Name: supplier_serial_supplier_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE supplier_serial_supplier_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.supplier_serial_supplier_seq OWNER TO biblivre;

--
-- Name: supplier_serial_supplier_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('supplier_serial_supplier_seq', 1, false);


--
-- Name: acquisition_supplier; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE acquisition_supplier (
    serial_supplier integer DEFAULT nextval('supplier_serial_supplier_seq'::regclass) NOT NULL,
    trade_mark_name character varying(100),
    company_name character varying(100),
    company_number character varying(18),
    vat_registration_number character varying(20),
    address character varying(70),
    number_address character varying(10),
    complement character varying(50),
    area character varying(30),
    city character varying(30),
    state character varying(2),
    country character varying(30),
    zip_code character varying(10),
    telephone_1 character varying(20),
    telephone_2 character varying(20),
    telephone_3 character varying(20),
    telephone_4 character varying(20),
    contact_1 character varying(30),
    contact_2 character varying(30),
    contact_3 character varying(30),
    contact_4 character varying(30),
    obs text,
    created timestamp without time zone,
    modified timestamp without time zone,
    url character varying(150),
    email character varying(100)
);


ALTER TABLE public.acquisition_supplier OWNER TO biblivre;

--
-- Name: authorities_record_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE authorities_record_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.authorities_record_serial_seq OWNER TO biblivre;

--
-- Name: authorities_record_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('authorities_record_serial_seq', 1, false);


--
-- Name: backups; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE backups (
    backuped timestamp without time zone NOT NULL
);


ALTER TABLE public.backups OWNER TO biblivre;

--
-- Name: cards_serial_card_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE cards_serial_card_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.cards_serial_card_seq OWNER TO biblivre;

--
-- Name: cards_serial_card_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('cards_serial_card_seq', 1, false);


--
-- Name: cards; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE cards (
    serial_card integer DEFAULT nextval('cards_serial_card_seq'::regclass) NOT NULL,
    card_number character varying(50) NOT NULL,
    userid integer NOT NULL,
    date_time timestamp without time zone,
    status smallint NOT NULL
);


ALTER TABLE public.cards OWNER TO biblivre;

--
-- Name: cataloging_authorities; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE cataloging_authorities (
    record_serial integer DEFAULT nextval('authorities_record_serial_seq'::regclass) NOT NULL,
    record text NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone
);


ALTER TABLE public.cataloging_authorities OWNER TO biblivre;

--
-- Name: cataloging_biblio; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE cataloging_biblio (
    record_serial integer NOT NULL,
    record text NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    material_type character varying(12),
    database smallint DEFAULT 0 NOT NULL
);


ALTER TABLE public.cataloging_biblio OWNER TO biblivre;

--
-- Name: cataloging_biblio_record_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE cataloging_biblio_record_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.cataloging_biblio_record_serial_seq OWNER TO biblivre;

--
-- Name: cataloging_biblio_record_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE cataloging_biblio_record_serial_seq OWNED BY cataloging_biblio.record_serial;


--
-- Name: cataloging_biblio_record_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('cataloging_biblio_record_serial_seq', 1, false);


--
-- Name: cataloging_holdings; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE cataloging_holdings (
    holding_serial integer NOT NULL,
    record_serial integer NOT NULL,
    record text NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone,
    availability integer DEFAULT 0 NOT NULL,
    database smallint DEFAULT 0 NOT NULL,
    asset_holding character varying(100) NOT NULL,
    loc_d character varying(100)
);


ALTER TABLE public.cataloging_holdings OWNER TO biblivre;

--
-- Name: cataloging_vocabulary; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE cataloging_vocabulary (
    record_serial integer NOT NULL,
    record text NOT NULL,
    created timestamp without time zone,
    modified timestamp without time zone
);


ALTER TABLE public.cataloging_vocabulary OWNER TO biblivre;

--
-- Name: code_users_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE code_users_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.code_users_seq OWNER TO biblivre;

--
-- Name: code_users_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('code_users_seq', 1, false);


--
-- Name: digital_media; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE digital_media (
    id integer NOT NULL,
    file bytea,
    mime_type character varying(64),
    file_name character varying(64)
);


ALTER TABLE public.digital_media OWNER TO biblivre;

--
-- Name: digital_media_id_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE digital_media_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.digital_media_id_seq OWNER TO biblivre;

--
-- Name: digital_media_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE digital_media_id_seq OWNED BY digital_media.id;


--
-- Name: digital_media_id_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('digital_media_id_seq', 1, false);


--
-- Name: file_name_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE file_name_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.file_name_seq OWNER TO biblivre;

--
-- Name: file_name_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('file_name_seq', 1, false);



--
-- Name: holding_creation_counter; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE holding_creation_counter (
    serial integer NOT NULL,
    creation_date date NOT NULL,
    user_name character varying(255) NOT NULL,
    user_login character varying(100),
    user_id integer NOT NULL
);


ALTER TABLE public.holding_creation_counter OWNER TO biblivre;

--
-- Name: holding_creation_counter_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE holding_creation_counter_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.holding_creation_counter_serial_seq OWNER TO biblivre;

--
-- Name: holding_creation_counter_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE holding_creation_counter_serial_seq OWNED BY holding_creation_counter.serial;


--
-- Name: holding_creation_counter_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('holding_creation_counter_serial_seq', 1, false);


--
-- Name: holdings_holding_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE holdings_holding_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.holdings_holding_serial_seq OWNER TO biblivre;

--
-- Name: holdings_holding_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE holdings_holding_serial_seq OWNED BY cataloging_holdings.holding_serial;


--
-- Name: holdings_holding_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('holdings_holding_serial_seq', 1, false);



--
-- Name: idx_any; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_any (
    record_serial integer NOT NULL,
    index_word character varying(100)
);


ALTER TABLE public.idx_any OWNER TO biblivre;

--
-- Name: idx_author; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_author (
    record_serial integer NOT NULL,
    index_word character varying(100)
);


ALTER TABLE public.idx_author OWNER TO biblivre;

--
-- Name: idx_authorities; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_authorities (
    index_id integer NOT NULL,
    index_word character varying(100) NOT NULL,
    record_serial integer NOT NULL
);


ALTER TABLE public.idx_authorities OWNER TO biblivre;

--
-- Name: idx_authorities_index_id_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE idx_authorities_index_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.idx_authorities_index_id_seq OWNER TO biblivre;

--
-- Name: idx_authorities_index_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE idx_authorities_index_id_seq OWNED BY idx_authorities.index_id;


--
-- Name: idx_authorities_index_id_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('idx_authorities_index_id_seq', 1, false);


--
-- Name: idx_isbn; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_isbn (
    record_serial integer NOT NULL,
    index_word character varying(100)
);


ALTER TABLE public.idx_isbn OWNER TO biblivre;

--
-- Name: idx_sort_authorities; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_sort_authorities (
    record_serial integer NOT NULL,
    index_word character varying(512) NOT NULL
);


ALTER TABLE public.idx_sort_authorities OWNER TO biblivre;

--
-- Name: idx_sort_biblio; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_sort_biblio (
    record_serial integer NOT NULL,
    index_word character varying(512) NOT NULL
);


ALTER TABLE public.idx_sort_biblio OWNER TO biblivre;

--
-- Name: idx_sort_vocabulary; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_sort_vocabulary (
    record_serial integer NOT NULL,
    index_word character varying(512) NOT NULL
);


ALTER TABLE public.idx_sort_vocabulary OWNER TO biblivre;

--
-- Name: idx_subject; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_subject (
    record_serial integer NOT NULL,
    index_word character varying(100)
);


ALTER TABLE public.idx_subject OWNER TO biblivre;

--
-- Name: idx_title; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_title (
    record_serial integer NOT NULL,
    index_word character varying(100)
);


ALTER TABLE public.idx_title OWNER TO biblivre;

--
-- Name: idx_vocabulary; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_vocabulary (
    index_id integer NOT NULL,
    index_word character varying(100) NOT NULL,
    record_serial integer NOT NULL,
    control_field character(3) NOT NULL
);


ALTER TABLE public.idx_vocabulary OWNER TO biblivre;

--
-- Name: idx_vocabulary_index_id_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE idx_vocabulary_index_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.idx_vocabulary_index_id_seq OWNER TO biblivre;

--
-- Name: idx_vocabulary_index_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE idx_vocabulary_index_id_seq OWNED BY idx_vocabulary.index_id;


--
-- Name: idx_vocabulary_index_id_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('idx_vocabulary_index_id_seq', 1, false);


--
-- Name: idx_year; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE idx_year (
    record_serial integer NOT NULL,
    index_word character varying(100)
);


ALTER TABLE public.idx_year OWNER TO biblivre;


--
-- Name: labels; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE labels (
    label_serial integer NOT NULL,
    holding_serial integer NOT NULL,
    author character varying(200),
    title character varying(900),
    location_a character varying(100),
    location_b character varying(100),
    location_c character varying(100),
    record_serial integer NOT NULL,
    location_d character varying(100),
    asset_holding character varying(100)
);


ALTER TABLE public.labels OWNER TO biblivre;

--
-- Name: labels_record_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE labels_record_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.labels_record_serial_seq OWNER TO biblivre;

--
-- Name: labels_record_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE labels_record_serial_seq OWNED BY labels.label_serial;


--
-- Name: labels_record_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('labels_record_serial_seq', 1, false);



--
-- Name: lending; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE lending (
    lending_serial integer NOT NULL,
    holding_serial integer NOT NULL,
    user_serial integer NOT NULL,
    lending_date date NOT NULL,
    return_date date NOT NULL
);


ALTER TABLE public.lending OWNER TO biblivre;

--
-- Name: lending_fine; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE lending_fine (
    serial integer NOT NULL,
    user_serial integer NOT NULL,
    lending_history_serial integer NOT NULL,
    value real NOT NULL,
    payment date
);


ALTER TABLE public.lending_fine OWNER TO biblivre;

--
-- Name: lending_fine_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE lending_fine_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.lending_fine_serial_seq OWNER TO biblivre;

--
-- Name: lending_fine_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE lending_fine_serial_seq OWNED BY lending_fine.serial;


--
-- Name: lending_fine_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('lending_fine_serial_seq', 1, false);


--
-- Name: lending_history; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE lending_history (
    lending_history_serial integer NOT NULL,
    holding_serial integer NOT NULL,
    user_serial integer NOT NULL,
    lending_date date NOT NULL,
    return_date date NOT NULL
);


ALTER TABLE public.lending_history OWNER TO biblivre;

--
-- Name: lending_history_lending_history_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE lending_history_lending_history_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.lending_history_lending_history_serial_seq OWNER TO biblivre;

--
-- Name: lending_history_lending_history_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE lending_history_lending_history_serial_seq OWNED BY lending_history.lending_history_serial;


--
-- Name: lending_history_lending_history_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('lending_history_lending_history_serial_seq', 1, false);


--
-- Name: lending_lending_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE lending_lending_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.lending_lending_serial_seq OWNER TO biblivre;

--
-- Name: lending_lending_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE lending_lending_serial_seq OWNED BY lending.lending_serial;


--
-- Name: lending_lending_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('lending_lending_serial_seq', 1, false);


--
-- Name: logins_loginid_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE logins_loginid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.logins_loginid_seq OWNER TO biblivre;

--
-- Name: logins_loginid_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('logins_loginid_seq', 1, true);


--
-- Name: logins; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE logins (
    loginid integer DEFAULT nextval('logins_loginid_seq'::regclass) NOT NULL,
    loginname character varying(20),
    encpwd text
);


ALTER TABLE public.logins OWNER TO biblivre;

--
-- Name: module_module_id_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE module_module_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.module_module_id_seq OWNER TO biblivre;

--
-- Name: module_module_id_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('module_module_id_seq', 1, false);


--
-- Name: permissions; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE permissions (
    loginid integer NOT NULL,
    permission character varying(50) NOT NULL
);


ALTER TABLE public.permissions OWNER TO biblivre;

--
-- Name: reservation; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE reservation (
    reservation_serial integer NOT NULL,
    record_serial integer,
    userid integer NOT NULL,
    created timestamp without time zone,
    expires timestamp without time zone NOT NULL
);


ALTER TABLE public.reservation OWNER TO biblivre;

--
-- Name: reservation_reservation_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE reservation_reservation_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.reservation_reservation_serial_seq OWNER TO biblivre;

--
-- Name: reservation_reservation_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE reservation_reservation_serial_seq OWNED BY reservation.reservation_serial;


--
-- Name: reservation_reservation_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('reservation_reservation_serial_seq', 1, false);



--
-- Name: search_counter; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE search_counter (
    serial integer NOT NULL,
    search_date timestamp without time zone
);


ALTER TABLE public.search_counter OWNER TO biblivre;

--
-- Name: search_counter_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE search_counter_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.search_counter_serial_seq OWNER TO biblivre;

--
-- Name: search_counter_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE search_counter_serial_seq OWNED BY search_counter.serial;


--
-- Name: search_counter_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('search_counter_serial_seq', 1, false);


--
-- Name: station_serial_station_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE station_serial_station_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.station_serial_station_seq OWNER TO biblivre;

--
-- Name: station_serial_station_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('station_serial_station_seq', 1, false);



--
-- Name: station; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE station (
    serial_station integer DEFAULT nextval('station_serial_station_seq'::regclass) NOT NULL,
    description text,
    creation_date timestamp without time zone NOT NULL,
    changed_date timestamp without time zone NOT NULL,
    status character(2) NOT NULL,
    ip character(15) NOT NULL
);


ALTER TABLE public.station OWNER TO biblivre;

--
-- Name: users; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE users (
    userid integer DEFAULT nextval('code_users_seq'::regclass) NOT NULL,
    signup_date date NOT NULL,
    alter_date date NOT NULL,
    renew_date date NOT NULL,
    username character varying(256) NOT NULL,
    social_id_number character varying(50),
    dlicense character varying(50),
    type_id character varying(50),
    birthday date,
    marital_status character varying(50),
    sex character(1),
    occupatio character varying(50),
    father_name character varying(256),
    mother_name character varying(256),
    reference_1 character varying(256),
    tel_ref_1 character varying(50),
    reference_2 character varying(255),
    tel_ref_2 character varying(50),
    email character varying(256),
    user_type integer,
    obs text,
    country character varying(50),
    state character varying(50),
    city character varying(50),
    address character varying(256),
    number character varying(50),
    completion character varying(50),
    zip_code character varying(50),
    status character varying(50),
    whosignup integer NOT NULL,
    loginid integer,
    cellphone character varying(50),
    extension_line character varying(50),
    usernameascii character varying(256),
    photo_id character varying(255)
);


ALTER TABLE public.users OWNER TO biblivre;


--
-- Name: users_cards; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE users_cards (
    serial_card integer NOT NULL,
    user_name character varying(100),
    user_type character varying(50),
    user_id integer
);


ALTER TABLE public.users_cards OWNER TO biblivre;

--
-- Name: users_cards_serial_card_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE users_cards_serial_card_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.users_cards_serial_card_seq OWNER TO biblivre;

--
-- Name: users_cards_serial_card_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE users_cards_serial_card_seq OWNED BY users_cards.serial_card;


--
-- Name: users_cards_serial_card_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('users_cards_serial_card_seq', 1, false);


--
-- Name: users_idx_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE users_idx_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.users_idx_seq OWNER TO biblivre;

--
-- Name: users_idx_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('users_idx_seq', 1, false);



--
-- Name: users_type; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE users_type (
    serial integer NOT NULL,
    description character varying(255),
    number_max_itens integer,
    time_returned integer,
    usertype character varying(64) NOT NULL,
    max_reservation_days integer
);


ALTER TABLE public.users_type OWNER TO biblivre;

--
-- Name: users_type_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE users_type_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.users_type_serial_seq OWNER TO biblivre;

--
-- Name: users_type_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE users_type_serial_seq OWNED BY users_type.serial;


--
-- Name: users_type_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('users_type_serial_seq', 1, false);


CREATE TABLE "versions" (
   installed_versions character varying(100) NOT NULL, 
   CONSTRAINT "PK_versions" PRIMARY KEY (installed_versions)
);
ALTER TABLE "versions" OWNER TO biblivre;

--
-- Name: vocabulary_vocabulary_serial_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE vocabulary_vocabulary_serial_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.vocabulary_vocabulary_serial_seq OWNER TO biblivre;

--
-- Name: vocabulary_vocabulary_serial_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: biblivre
--

ALTER SEQUENCE vocabulary_vocabulary_serial_seq OWNED BY cataloging_vocabulary.record_serial;


--
-- Name: vocabulary_vocabulary_serial_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('vocabulary_vocabulary_serial_seq', 1, false);


--
-- Name: z3950_server_server_id_seq; Type: SEQUENCE; Schema: public; Owner: biblivre
--

CREATE SEQUENCE z3950_server_server_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.z3950_server_server_id_seq OWNER TO biblivre;

--
-- Name: z3950_server_server_id_seq; Type: SEQUENCE SET; Schema: public; Owner: biblivre
--

SELECT pg_catalog.setval('z3950_server_server_id_seq', 1, false);


--
-- Name: z3950_server; Type: TABLE; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE TABLE z3950_server (
    server_id integer DEFAULT nextval('z3950_server_server_id_seq'::regclass) NOT NULL,
    server_name character varying(100) NOT NULL,
    server_url character varying(100) NOT NULL,
    server_port integer NOT NULL,
    server_dbname character varying(30),
    server_charset character varying(30) DEFAULT 'UTF-8'::character varying
);


ALTER TABLE public.z3950_server OWNER TO biblivre;

--
-- Name: serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE access_control ALTER COLUMN serial SET DEFAULT nextval('access_control_serial_seq'::regclass);


--
-- Name: record_serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE cataloging_biblio ALTER COLUMN record_serial SET DEFAULT nextval('cataloging_biblio_record_serial_seq'::regclass);


--
-- Name: holding_serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE cataloging_holdings ALTER COLUMN holding_serial SET DEFAULT nextval('holdings_holding_serial_seq'::regclass);


--
-- Name: record_serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE cataloging_vocabulary ALTER COLUMN record_serial SET DEFAULT nextval('vocabulary_vocabulary_serial_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE digital_media ALTER COLUMN id SET DEFAULT nextval('digital_media_id_seq'::regclass);


--
-- Name: serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE holding_creation_counter ALTER COLUMN serial SET DEFAULT nextval('holding_creation_counter_serial_seq'::regclass);


--
-- Name: index_id; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE idx_authorities ALTER COLUMN index_id SET DEFAULT nextval('idx_authorities_index_id_seq'::regclass);


--
-- Name: index_id; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE idx_vocabulary ALTER COLUMN index_id SET DEFAULT nextval('idx_vocabulary_index_id_seq'::regclass);


--
-- Name: label_serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE labels ALTER COLUMN label_serial SET DEFAULT nextval('labels_record_serial_seq'::regclass);


--
-- Name: lending_serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE lending ALTER COLUMN lending_serial SET DEFAULT nextval('lending_lending_serial_seq'::regclass);


--
-- Name: serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE lending_fine ALTER COLUMN serial SET DEFAULT nextval('lending_fine_serial_seq'::regclass);


--
-- Name: lending_history_serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE lending_history ALTER COLUMN lending_history_serial SET DEFAULT nextval('lending_history_lending_history_serial_seq'::regclass);


--
-- Name: reservation_serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE reservation ALTER COLUMN reservation_serial SET DEFAULT nextval('reservation_reservation_serial_seq'::regclass);


--
-- Name: serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE search_counter ALTER COLUMN serial SET DEFAULT nextval('search_counter_serial_seq'::regclass);


--
-- Name: serial_card; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE users_cards ALTER COLUMN serial_card SET DEFAULT nextval('users_cards_serial_card_seq'::regclass);


--
-- Name: serial; Type: DEFAULT; Schema: public; Owner: biblivre
--

ALTER TABLE users_type ALTER COLUMN serial SET DEFAULT nextval('users_type_serial_seq'::regclass);


--
-- Data for Name: access_control; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: acquisition_item_quotation; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: acquisition_order; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: acquisition_quotation; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: acquisition_requisition; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: acquisition_supplier; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: backups; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: cards; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: cataloging_authorities; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: cataloging_biblio; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: cataloging_holdings; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: cataloging_vocabulary; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: digital_media; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: holding_creation_counter; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_any; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_author; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_authorities; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_isbn; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_sort_authorities; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_sort_biblio; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_sort_vocabulary; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_subject; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_title; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_vocabulary; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: idx_year; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: labels; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: lending; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: lending_fine; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: lending_history; Type: TABLE DATA; Schema: public; Owner: biblivre
--


--
-- Data for Name: permissions; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: reservation; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: search_counter; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: station; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: users_cards; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: users_type; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Data for Name: z3950_server; Type: TABLE DATA; Schema: public; Owner: biblivre
--



--
-- Name: DIGITAL_MEDIA_PK; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY digital_media
    ADD CONSTRAINT "DIGITAL_MEDIA_PK" PRIMARY KEY (id);


--
-- Name: PK_cataloging_biblio; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY cataloging_biblio
    ADD CONSTRAINT "PK_cataloging_biblio" PRIMARY KEY (record_serial);


--
-- Name: PK_idx_sort_biblio; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY idx_sort_biblio
    ADD CONSTRAINT "PK_idx_sort_biblio" PRIMARY KEY (record_serial);


--
-- Name: PK_permissions; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT "PK_permissions" PRIMARY KEY (loginid, permission);


--
-- Name: authorities_main_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY cataloging_authorities
    ADD CONSTRAINT authorities_main_pkey PRIMARY KEY (record_serial);


--
-- Name: backups_pk; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY backups
    ADD CONSTRAINT backups_pk PRIMARY KEY (backuped);


--
-- Name: card_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY cards
    ADD CONSTRAINT card_pkey PRIMARY KEY (serial_card);


--
-- Name: holding_creation_counter_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY holding_creation_counter
    ADD CONSTRAINT holding_creation_counter_pkey PRIMARY KEY (serial);


--
-- Name: holdings_pk; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY cataloging_holdings
    ADD CONSTRAINT holdings_pk PRIMARY KEY (holding_serial);


--
-- Name: idx_vocabulary_pk; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY idx_vocabulary
    ADD CONSTRAINT idx_vocabulary_pk PRIMARY KEY (index_id);


--
-- Name: item_quotation_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY acquisition_item_quotation
    ADD CONSTRAINT item_quotation_pkey PRIMARY KEY (serial_requisition, serial_quotation);


--
-- Name: labels_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY labels
    ADD CONSTRAINT labels_pkey PRIMARY KEY (label_serial);


--
-- Name: lending_fine_pk; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY lending_fine
    ADD CONSTRAINT lending_fine_pk PRIMARY KEY (serial);


--
-- Name: lending_history_pk; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY lending_history
    ADD CONSTRAINT lending_history_pk PRIMARY KEY (lending_history_serial);


--
-- Name: lending_pk; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY lending
    ADD CONSTRAINT lending_pk PRIMARY KEY (lending_serial);


--
-- Name: order_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY acquisition_order
    ADD CONSTRAINT order_pkey PRIMARY KEY (serial_order);


--
-- Name: pk_historico_cntrl_acesso; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY access_control
    ADD CONSTRAINT pk_historico_cntrl_acesso PRIMARY KEY (serial);


--
-- Name: pk_idx_authorities; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY idx_authorities
    ADD CONSTRAINT pk_idx_authorities PRIMARY KEY (index_id);


--
-- Name: pk_idx_sort_authorities; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY idx_sort_authorities
    ADD CONSTRAINT pk_idx_sort_authorities PRIMARY KEY (record_serial);


--
-- Name: pk_idx_sort_vocabulary; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY idx_sort_vocabulary
    ADD CONSTRAINT pk_idx_sort_vocabulary PRIMARY KEY (record_serial);


--
-- Name: pk_loginid; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY logins
    ADD CONSTRAINT pk_loginid PRIMARY KEY (loginid);


--
-- Name: quotation_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY acquisition_quotation
    ADD CONSTRAINT quotation_pkey PRIMARY KEY (serial_quotation);


--
-- Name: requisition_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY acquisition_requisition
    ADD CONSTRAINT requisition_pkey PRIMARY KEY (serial_requisition);


--
-- Name: reservation_serial_pk; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY reservation
    ADD CONSTRAINT reservation_serial_pk PRIMARY KEY (reservation_serial);


--
-- Name: search_counter_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY search_counter
    ADD CONSTRAINT search_counter_pkey PRIMARY KEY (serial);


--
-- Name: station_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY station
    ADD CONSTRAINT station_pkey PRIMARY KEY (serial_station);


--
-- Name: supplier_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY acquisition_supplier
    ADD CONSTRAINT supplier_pkey PRIMARY KEY (serial_supplier);


--
-- Name: unik_loginname; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY logins
    ADD CONSTRAINT unik_loginname UNIQUE (loginname);


--
-- Name: users_cards_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY users_cards
    ADD CONSTRAINT users_cards_pkey PRIMARY KEY (serial_card);


--
-- Name: users_pkey1; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey1 PRIMARY KEY (userid);


--
-- Name: users_type_pkey; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY users_type
    ADD CONSTRAINT users_type_pkey PRIMARY KEY (serial);


--
-- Name: vocabulary_serial_pk; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY cataloging_vocabulary
    ADD CONSTRAINT vocabulary_serial_pk PRIMARY KEY (record_serial);


--
-- Name: z3950_server_pk; Type: CONSTRAINT; Schema: public; Owner: biblivre; Tablespace: 
--

ALTER TABLE ONLY z3950_server
    ADD CONSTRAINT z3950_server_pk PRIMARY KEY (server_id);


--
-- Name: IX_biblio_database; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX "IX_biblio_database" ON cataloging_biblio USING btree (database, record_serial);


--
-- Name: IX_holding_asset; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE UNIQUE INDEX "IX_holding_asset" ON cataloging_holdings USING btree (asset_holding);


--
-- Name: IX_holding_biblio_loc; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX "IX_holding_biblio_loc" ON cataloging_holdings USING btree (record_serial, loc_d);


--
CREATE INDEX "IX_holdings_biblio" ON cataloging_holdings USING btree (record_serial, database);
-- Name: IX_reservation; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX "IX_reservation" ON reservation USING btree (record_serial, expires);


--
-- Name: access_control_idx_serial_reader; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX access_control_idx_serial_reader ON access_control USING btree (departure_datetime, serial_reader);


--
-- Name: control_field_index_word_idx; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX control_field_index_word_idx ON idx_vocabulary USING btree (control_field, index_word);


--
-- Name: idx_idx_any; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX idx_idx_any ON idx_any USING btree (index_word, record_serial);


--
-- Name: idx_idx_author; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX idx_idx_author ON idx_author USING btree (index_word, record_serial);


--
-- Name: idx_idx_authorities; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX idx_idx_authorities ON idx_authorities USING btree (index_word);


--
-- Name: idx_idx_isbn; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX idx_idx_isbn ON idx_isbn USING btree (index_word, record_serial);


--
-- Name: idx_idx_subject; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX idx_idx_subject ON idx_subject USING btree (index_word, record_serial);


--
-- Name: idx_idx_title; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX idx_idx_title ON idx_title USING btree (index_word, record_serial);


--
-- Name: idx_idx_year; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX idx_idx_year ON idx_year USING btree (index_word, record_serial);


--
-- Name: idx_vocabulary_word; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX idx_vocabulary_word ON idx_vocabulary USING btree (index_word);


--
-- Name: username_idx; Type: INDEX; Schema: public; Owner: biblivre; Tablespace: 
--

CREATE INDEX username_idx ON users USING btree (username);


--
-- Name: FK_loginid; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY users
    ADD CONSTRAINT "FK_loginid" FOREIGN KEY (loginid) REFERENCES logins(loginid);


--
-- Name: holding_fk; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY labels
    ADD CONSTRAINT holding_fk FOREIGN KEY (holding_serial) REFERENCES cataloging_holdings(holding_serial) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: idx_authorities_serial; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY idx_authorities
    ADD CONSTRAINT idx_authorities_serial FOREIGN KEY (record_serial) REFERENCES cataloging_authorities(record_serial) ON DELETE CASCADE;


--
-- Name: idx_vocabulary_vocabulary_serial; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY idx_vocabulary
    ADD CONSTRAINT idx_vocabulary_vocabulary_serial FOREIGN KEY (record_serial) REFERENCES cataloging_vocabulary(record_serial) ON DELETE CASCADE;


--
-- Name: lending_fine_lending_history_fk; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY lending_fine
    ADD CONSTRAINT lending_fine_lending_history_fk FOREIGN KEY (lending_history_serial) REFERENCES lending_history(lending_history_serial);


--
-- Name: lending_fine_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY lending_fine
    ADD CONSTRAINT lending_fine_user_fk FOREIGN KEY (user_serial) REFERENCES users(userid);


--
-- Name: lending_history_holding_fk; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY lending_history
    ADD CONSTRAINT lending_history_holding_fk FOREIGN KEY (holding_serial) REFERENCES cataloging_holdings(holding_serial);


--
-- Name: lending_history_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY lending_history
    ADD CONSTRAINT lending_history_user_fk FOREIGN KEY (user_serial) REFERENCES users(userid);


--
-- Name: lending_holding_fk; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY lending
    ADD CONSTRAINT lending_holding_fk FOREIGN KEY (holding_serial) REFERENCES cataloging_holdings(holding_serial);


--
-- Name: lending_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY lending
    ADD CONSTRAINT lending_user_fk FOREIGN KEY (user_serial) REFERENCES users(userid);


--
-- Name: quotation_fk; Type: FK CONSTRAINT; Schema: public; Owner: biblivre
--

ALTER TABLE ONLY acquisition_item_quotation
    ADD CONSTRAINT quotation_fk FOREIGN KEY (serial_quotation) REFERENCES acquisition_quotation(serial_quotation) ON DELETE CASCADE;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--


INSERT INTO logins (loginid, loginname, encpwd) VALUES (1, 'admin', 'C4wx3TpMHnSwdk1bUQ/V6qwAQmw=');

INSERT INTO users_type (description, number_max_itens, time_returned, usertype, max_reservation_days) VALUES ('Leitores', 3, 15, 'Leitor', 10);
INSERT INTO users_type (description, number_max_itens, time_returned, usertype, max_reservation_days) VALUES ('Administradores', 99, 365, 'Administrador', 365);


INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('Universidad de Chile - Santiago, Chile', 'unicornio.uchile.cl', 2200, 'default', 'MARC-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('Université Laval - Quebec, Canadá', 'ariane2.ulaval.ca', 2200, 'default', 'MARC-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('Brunel University - Londres, Reino Unido', 'library.brunel.ac.uk', 2200, 'default', 'UTF-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('Acadia University - Nova Escócia, Canada', 'jasper.acadiau.ca', 2200, 'default', 'UTF-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('Carnegie Mellon University - Pittsburgh, PA - EUA', 'webcat.library.cmu.edu', 2200, 'unicorn', 'UTF-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('New York Public Library - EUA', 'catalog.nypl.org', 210, 'INNOPAC', 'UTF-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('Biblioteca Nacional da Espanha - Madrid', 'sigb.bne.es', 2200, 'default', 'UTF-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('Library of Congress Online Catalog - EUA', '140.147.249.67', 210, 'LCDB', 'UTF-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('South University New Orleans, EUA', 'suno.louislibraries.org', 7705, 'default', 'MARC-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('Penn State University, EUA', 'zcat.libraries.psu.edu', 2200, 'default', 'UTF-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('The Fletcher School, Tufts University, EUA', 'fletcher.louislibraries.org', 8205, 'default', 'UTF-8');
INSERT INTO z3950_server (server_name, server_url, server_port, server_dbname, server_charset) VALUES ('Univerdidad de Madrid, Espanha', 'marte.biblioteca.upm.es', 2200, 'default', 'MARC-8');


INSERT INTO versions (installed_versions) VALUES ('3.0.0');
INSERT INTO versions (installed_versions) VALUES ('3.0.1');
INSERT INTO versions (installed_versions) VALUES ('3.0.2');
INSERT INTO versions (installed_versions) VALUES ('3.0.3');
INSERT INTO versions (installed_versions) VALUES ('3.0.4');
INSERT INTO versions (installed_versions) VALUES ('3.0.5');
INSERT INTO versions (installed_versions) VALUES ('3.0.6');
INSERT INTO versions (installed_versions) VALUES ('3.0.7');
INSERT INTO versions (installed_versions) VALUES ('3.0.8');
INSERT INTO versions (installed_versions) VALUES ('3.0.9');
INSERT INTO versions (installed_versions) VALUES ('3.0.10');
INSERT INTO versions (installed_versions) VALUES ('3.0.11');
INSERT INTO versions (installed_versions) VALUES ('3.0.12');
INSERT INTO versions (installed_versions) VALUES ('3.0.13');
INSERT INTO versions (installed_versions) VALUES ('3.0.14');