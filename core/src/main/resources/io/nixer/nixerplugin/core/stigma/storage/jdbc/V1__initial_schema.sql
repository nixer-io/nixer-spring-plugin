--
-- Initial stigma storage DB schema definition
--

CREATE TABLE IF NOT EXISTS stigmas
(
    guid            UUID NOT NULL DEFAULT RANDOM_UUID() PRIMARY KEY,
    stigma_value    VARCHAR(255) UNIQUE NOT NULL,
    status          VARCHAR(128) NOT NULL,
    creation_date   TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS stigma_value_idx ON stigmas(stigma_value);
