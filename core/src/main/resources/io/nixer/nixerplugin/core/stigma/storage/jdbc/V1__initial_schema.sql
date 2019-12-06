--
-- Initial stigma storage DB schema definition
--

CREATE TABLE IF NOT EXISTS stigmas
(
    guid         UUID NOT NULL PRIMARY KEY,
    stigma_value VARCHAR(255) UNIQUE NOT NULL,
    status       VARCHAR(128) NOT NULL
);
