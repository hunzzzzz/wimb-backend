----------------------------------------------------------------------------------------
-- SCHEMA
----------------------------------------------------------------------------------------
CREATE SCHEMA IF NOT EXISTS wimb;

----------------------------------------------------------------------------------------
-- USER
----------------------------------------------------------------------------------------
CREATE TABLE wimb.user
(
    user_id     VARCHAR(50) PRIMARY KEY,
    login_id    VARCHAR(50)  NOT NULL,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(100) NOT NULL,
    role        VARCHAR(20)  NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    file_id     VARCHAR(50),
    description TEXT,
    visit_count INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP,

    CONSTRAINT uk_user_login_id UNIQUE (login_id),
    CONSTRAINT uk_user_email UNIQUE (email)
);

----------------------------------------------------------------------------------------
-- POST_CATEGORY
----------------------------------------------------------------------------------------
CREATE SEQUENCE wimb.seq_post_category
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE wimb.post_category
(
    post_category_id BIGINT PRIMARY KEY   DEFAULT nextval('wimb.seq_post_category'),
    user_id          VARCHAR(50) NOT NULL,
    name             VARCHAR(50) NOT NULL,
    sort_order       INT         NOT NULL,
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_category_user FOREIGN KEY (user_id) REFERENCES wimb.user (user_id)
);

ALTER SEQUENCE wimb.seq_post_category OWNED BY wimb.post_category.post_category_id;

----------------------------------------------------------------------------------------
-- POST_BASE
----------------------------------------------------------------------------------------
CREATE TABLE wimb.post_base
(
    post_id     VARCHAR(50) PRIMARY KEY,
    category_id BIGINT       NOT NULL,
    user_id     VARCHAR(50)  NOT NULL,
    title       VARCHAR(255) NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    type        VARCHAR(20)  NOT NULL,
    visibility  VARCHAR(20)  NOT NULL,
    view_count  INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_base_category FOREIGN KEY (category_id) REFERENCES wimb.post_category (post_category_id),
    CONSTRAINT fk_post_base_user FOREIGN KEY (user_id) REFERENCES wimb.user (user_id)
);

----------------------------------------------------------------------------------------
-- POST_TAG
----------------------------------------------------------------------------------------
CREATE SEQUENCE wimb.seq_post_tag
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE wimb.post_tag
(
    seq_no     BIGINT PRIMARY KEY   DEFAULT nextval('wimb.seq_post_tag'),
    post_id    VARCHAR(50) NOT NULL,
    name       VARCHAR(50) NOT NULL,
    sort_order INT         NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    CONSTRAINT fk_post_tag_post_base FOREIGN KEY (post_id) REFERENCES wimb.post_base (post_id)
);

ALTER SEQUENCE wimb.seq_post_tag OWNED BY wimb.post_tag.seq_no;

----------------------------------------------------------------------------------------
-- POST_SNIPPET
----------------------------------------------------------------------------------------
CREATE TABLE wimb.post_snippet
(
    post_id     VARCHAR(50) PRIMARY KEY,
    code        TEXT        NOT NULL,
    description TEXT        NOT NULL,
    language    VARCHAR(30) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_snippet_post_base FOREIGN KEY (post_id) REFERENCES wimb.post_base (post_id)
);

----------------------------------------------------------------------------------------
-- POST_NORMAL
----------------------------------------------------------------------------------------
CREATE TABLE wimb.post_normal
(
    post_id       VARCHAR(50) PRIMARY KEY,
    content       TEXT      NOT NULL,
    plain_content TEXT      NOT NULL,
    file_id       VARCHAR(50),
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_normal_post_base FOREIGN KEY (post_id) REFERENCES wimb.post_base (post_id)
);

----------------------------------------------------------------------------------------
-- COMMON_FILE
----------------------------------------------------------------------------------------
CREATE TABLE wimb.common_file
(
    file_id            VARCHAR(50)  NOT NULL,
    file_seq_no        INT          NOT NULL,
    user_id            VARCHAR(50)  NOT NULL,
    status             VARCHAR(20)  NOT NULL,
    file_url           TEXT         NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    size               BIGINT       NOT NULL,
    content_type       VARCHAR(100) NOT NULL,
    identifier         VARCHAR(100) NOT NULL,
    s3_key             VARCHAR(500) NOT NULL,
    represent_yn       CHAR(1)      NOT NULL,
    started_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_at        TIMESTAMP,
    updated_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_common_file PRIMARY KEY (file_id, file_seq_no),
    CONSTRAINT fk_common_file_user FOREIGN KEY (user_id) REFERENCES wimb.user (user_id)
);

CREATE UNIQUE INDEX uk_common_file_represent
    ON wimb.common_file (file_id)
    WHERE represent_yn = 'Y'
;

----------------------------------------------------------------------------------------
-- IDENTIFICATION
----------------------------------------------------------------------------------------
CREATE SEQUENCE wimb.seq_identification
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE wimb.identification
(
    seq_no     BIGINT PRIMARY KEY    DEFAULT nextval('wimb.seq_identification'),
    email      VARCHAR(100) NOT NULL,
    code       VARCHAR(20)  NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    purpose    VARCHAR(20)  NOT NULL,
    started_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP    NOT NULL,
    fail_count INT          NOT NULL DEFAULT 0,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER SEQUENCE wimb.seq_identification OWNED BY wimb.identification.seq_no;

----------------------------------------------------------------------------------------
-- API_RESULT
----------------------------------------------------------------------------------------
CREATE SEQUENCE wimb.seq_api_result
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE wimb.api_result
(
    seq_no       BIGINT PRIMARY KEY   DEFAULT nextval('wimb.seq_api_result'),
    type         VARCHAR(20) NOT NULL,
    email        VARCHAR(100),
    s3_key       VARCHAR(500),
    status       VARCHAR(20) NOT NULL,
    fail_message VARCHAR(255),
    requested_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP
);

ALTER SEQUENCE wimb.seq_api_result OWNED BY wimb.api_result.seq_no;
