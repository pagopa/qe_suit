package it.pagopa.send.common.informal_notification.domain;

/**
 * Magic Strings, Mock e Mock Sequences per test automatici su comunicazioni informali (bonarie) e canali.
 */
public final class InformalMockSequences {

    private InformalMockSequences() {}

    // --- IO ---
    public static final String IO_CF_NO_IO = "CLMCST42R12D969Z";

    // --- EMAIL ---
    public static final String EMAIL_BOUNCE = "bounce@simulator.amazonses.com";
    public static final String EMAIL_COMPLAINT = "complaint@simulator.amazonses.com";

    // --- SMS ---
    public static final String SMS_OK_PREFIX = "+3900000";
    public static final String SMS_KO_PREFIX = "+39001";
    public static final String SMS_OK_SAMPLE = "+390000012345";
    public static final String SMS_KO_SAMPLE = "+390011234567";

    // --- PEC (Email Mock Sequences) ---
    public static final String PEC_OK_FAST = "OK-pecSuccess.it@sequence.5s-C000.5s-C001.5s-C003";
    public static final String PEC_KO_IMMEDIATE = "FAIL-pecFirstKO.it@sequence.5s-C000.5s-C001.5s-C004";
    public static final String PEC_RETRY_SUCCESS = "OK-pecFirstFailSecondSuccess.it@sequence.5s-C000.5s-C001.5s-C004attempt5s-C000.5s-C001.5s-C005.5s-C003";
    public static final String PEC_DOUBLE_KO = "FAIL-pecFirstK0SecondKO.it@sequence.5s-C000.5s-C001.5s-C004attempt5s-C000.5s-C001.5s-C005.5s-C004";
    public static final String PEC_SLOW_120S = "OK-PEC-SLOW-2.it@sequence.120s-C000.120s-C001.120s-C005.120s-C003";
    public static final String PEC_SLOW_90S = "OK-PEC-SLOW.it@sequence.90s-C000.90s-C001.90s-C005.90s-C003";

    // --- ANALOG (Postale Mock Sequences - Messa in Mora) ---
    public static final String ANALOG_RS_OK = "@sequence.5s-CON080.5s-RECRS001C";
    public static final String ANALOG_RS_KO = "@sequence.5s-CON080.5s-RECRS002A[FAILCAUSE:M07].5s-RECRS002B[DOC:Plico].5s-RECRS002C";
    public static final String ANALOG_RS_RETRY = "@sequence.5s-CON080.5s-RECRS006[FAILCAUSE:F03]@retry.5s-CON080.5s-RECRS001C";
    public static final String ANALOG_RIS_OK = "@sequence.5s-CON080.5s-RECRSI001.5s-RECRS1002.5s-RECRSI003C";
    public static final String ANALOG_RIS_KO = "@sequence.5s-CON080.5s-RECRS1001.5s-RECRSI002.5s-RECRS1004A.5s-RECRS1004B[DOC:Plico].5s-RECRS1004C";

    // --- Standard Campaigns ---
    public static final String CAMPAIGN_FATTORD = "Fattord";
    public static final String CAMPAIGN_REMINDER = "Reminder";
    public static final String CAMPAIGN_MESSAMORA = "MessaMora";
}
