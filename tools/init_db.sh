#!/bin/bash

IS_INIT_DATA=$1

tmpfile=$(mktemp)

trap "rm -rf ${tmpfile}" EXIT

cat <<'__EOS__' > ${tmpfile}
CREATE SEQUENCE IF NOT EXISTS t_member_seq AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE;

CREATE TABLE IF NOT EXISTS t_user (
    username        VARCHAR(255)  NOT NULL,
    password        VARCHAR(255)  NOT NULL,
    enabled         BOOLEAN       NOT NULL,
    gmail           VARCHAR(255)  UNIQUE,
    PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS t_member (
    member_id	    BIGINT NOT NULL,
    mail	        VARCHAR(255) NOT NULL UNIQUE,
    name	        VARCHAR(31) NOT NULL,
    address	        VARCHAR(127) NOT NULL,
    start_date	    DATE NOT NULL,
    end_date	    DATE,
    payment_method	INTEGER NOT NULL,
    created_at	    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at	    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (member_id)
);

CREATE SEQUENCE IF NOT EXISTS t_charge_seq AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE;

CREATE TABLE IF NOT EXISTS t_charge (
    charge_id       BIGINT NOT NULL,
    name            VARCHAR(127) NOT NULL,
    amount          NUMERIC(9,0) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (charge_id)
);

CREATE TABLE IF NOT EXISTS t_billing_status (
    billing_ym      DATE NOT NULL,
    is_commit       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (billing_ym)
);

CREATE TABLE IF NOT EXISTS t_billing_data (
    billing_ym      DATE NOT NULL,
    member_id       BIGINT NOT NULL,
    mail            VARCHAR(255) NOT NULL,
    name            VARCHAR(31) NOT NULL,
    address         VARCHAR(127) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE,
    payment_method  INTEGER NOT NULL,
    amount          NUMERIC(10,0) NOT NULL,
    tax_ratio       NUMERIC(5,2) NOT NULL,
    total           NUMERIC(10,0) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (billing_ym, member_id),
    FOREIGN KEY (billing_ym)
        REFERENCES t_billing_status(billing_ym)
);

CREATE TABLE IF NOT EXISTS t_billing_detail_data (
    billing_ym      DATE NOT NULL,
    member_id       BIGINT NOT NULL,
    charge_id       BIGINT NOT NULL,
    name            VARCHAR(127) NOT NULL,
    amount          NUMERIC(9,0) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (billing_ym, member_id, charge_id),
    FOREIGN KEY (billing_ym, member_id)
        REFERENCES t_billing_data(billing_ym, member_id)
);

CREATE TABLE IF NOT EXISTS t_gmailuser_invation (
    username        VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    gmail           VARCHAR(255) NOT NULL,
    token           INT NOT NULL,
    expiration      TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '5 minutes'),
    PRIMARY KEY (username, gmail)
);

CREATE OR REPLACE FUNCTION prevent_gmail_insert_update() RETURNS trigger AS $$
BEGIN
    IF current_setting('app.gmail_update', true) = 'ON'
        THEN RETURN NEW;
    END IF;
    IF TG_OP = 'INSERT' AND NEW.gmail IS NOT NULL
        THEN RAISE EXCEPTION 'gmailの直接登録は禁止です';
    END IF;
    IF TG_OP = 'UPDATE' AND NEW.gmail IS DISTINCT FROM OLD.gmail
        THEN RAISE EXCEPTION 'gmailの直接更新は禁止です';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
DROP TRIGGER IF EXISTS trg_prevent_gmail ON t_user;
CREATE TRIGGER trg_prevent_gmail BEFORE INSERT OR UPDATE ON t_user FOR EACH ROW EXECUTE FUNCTION prevent_gmail_insert_update();


__EOS__

if [ "${IS_INIT_DATA}" = "init_data" ]; then
cat <<'__EOS__' >> ${tmpfile}
BEGIN;

DELETE FROM t_user;
DELETE FROM t_member;
DELETE FROM t_charge;
DELETE FROM t_billing_status;
DELETE FROM t_billing_data;
DELETE FROM t_billing_detail_data;
DELETE FROM t_gmailuser_invation;

-- user/password
INSERT INTO T_USER VALUES ('user', '$argon2id$v=19$m=14,t=2,p=1$argon2id$v=19$m=16384,t=2,p=1$RlFPZHlKa1k4OGRqTEtkNg$0OglBcOo6kATVL9zUpUaJGdPQN5oeh4Okinvqm80R7o', true);

INSERT INTO T_MEMBER VALUES (nextval('t_member_seq'), 'yamada@example.com', '山田　太郎', '東京都千代田区1-1-1', '2026-01-01', NULL, 1, NOW(), NOW());

COMMIT;
__EOS__
fi

cat ${tmpfile} | psql -h database -U trainingapp -W trainingapp