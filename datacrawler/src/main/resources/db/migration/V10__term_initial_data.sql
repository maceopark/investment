CREATE TABLE IF NOT EXISTS `datacrawl`.`TERM_VARIATION` (
  `TERM` VARCHAR(36) NOT NULL,
  `VARIATION` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`TERM`, `VARIATION`),
  CONSTRAINT `fk_TERM_VARIATION_FINANCIAL_SHEET_TERM1`
    FOREIGN KEY (`TERM`)
    REFERENCES `datacrawl`.`FINANCIAL_SHEET_TERM` (`TERM`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

insert into datacrawl.financial_sheet_term (term) values ('매출액');
insert into datacrawl.financial_sheet_term (term) values ('영업이익');
insert into datacrawl.financial_sheet_term (term) values ('세전계속사업이익');
insert into datacrawl.financial_sheet_term (term) values ('당기순이익');
insert into datacrawl.financial_sheet_term (term) values ('당기순이익(지배)');
insert into datacrawl.financial_sheet_term (term) values ('당기순이익(비지배)');
insert into datacrawl.financial_sheet_term (term) values ('자산총계');
insert into datacrawl.financial_sheet_term (term) values ('부채총계');
insert into datacrawl.financial_sheet_term (term) values ('자본총계');
insert into datacrawl.financial_sheet_term (term) values ('자본총계(지배)');
insert into datacrawl.financial_sheet_term (term) values ('자본총계(비지배)');
insert into datacrawl.financial_sheet_term (term) values ('자본금');
insert into datacrawl.financial_sheet_term (term) values ('영업활동현금흐름');
insert into datacrawl.financial_sheet_term (term) values ('투자활동현금흐름');
insert into datacrawl.financial_sheet_term (term) values ('재무활동현금흐름');
insert into datacrawl.financial_sheet_term (term) values ('CAPEX');
insert into datacrawl.financial_sheet_term (term) values ('FCF');
insert into datacrawl.financial_sheet_term (term) values ('이자발생부채');
insert into datacrawl.financial_sheet_term (term) values ('영업이익률');
insert into datacrawl.financial_sheet_term (term) values ('순이익률');
insert into datacrawl.financial_sheet_term (term) values ('ROE');
insert into datacrawl.term_variation (term, variation) values ('ROE', 'ROE(%)');
insert into datacrawl.financial_sheet_term (term) values ('ROA');
insert into datacrawl.term_variation (term, variation) values ('ROA', 'ROA(%)');
insert into datacrawl.financial_sheet_term (term) values ('부채비율');
insert into datacrawl.financial_sheet_term (term) values ('자본유보율');
insert into datacrawl.financial_sheet_term (term) values ('EPS');
insert into datacrawl.term_variation (term, variation) values ('EPS', 'EPS(원)');
insert into datacrawl.financial_sheet_term (term) values ('PER');
insert into datacrawl.term_variation (term, variation) values ('PER', 'PER(배)');
insert into datacrawl.financial_sheet_term (term) values ('BPS');
insert into datacrawl.term_variation (term, variation) values ('BPS', 'BPS(원)');
insert into datacrawl.financial_sheet_term (term) values ('PBR');
insert into datacrawl.term_variation (term, variation) values ('PBR', 'PBR(배)');
insert into datacrawl.financial_sheet_term (term) values ('현금DPS');
insert into datacrawl.term_variation (term, variation) values ('현금DPS', '현금DPS(원)');
insert into datacrawl.financial_sheet_term (term) values ('현금배당수익률');
insert into datacrawl.financial_sheet_term (term) values ('현금배당성향');
insert into datacrawl.term_variation (term, variation) values ('현금배당성향', '현금배당성향(%)');
insert into datacrawl.financial_sheet_term (term) values ('발행주식수');

commit;

