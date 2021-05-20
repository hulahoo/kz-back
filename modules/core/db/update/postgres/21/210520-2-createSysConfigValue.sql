CREATE OR REPLACE FUNCTION sys_config_value(character varying)
 RETURNS character varying
 LANGUAGE sql
 IMMUTABLE STRICT
AS $function$
select s.value_::varchar from SYS_CONFIG s where s.name = $1;
$function$