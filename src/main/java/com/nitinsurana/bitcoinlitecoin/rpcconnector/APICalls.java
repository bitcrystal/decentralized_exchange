/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nitinsurana.bitcoinlitecoin.rpcconnector;

import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public enum APICalls {

//    public static final Logger LOG = Logger.getLogger(APICalls.class.getName());
//
//    public static final String GET_NEW_ADDRESS = "";
//    public static final String DUMP_PRIVATE_KEY = "";
    GET_NEW_ADDRESS("getnewaddress"),
    DUMP_PRIVATE_KEY("dumpprivkey"),
    IMPORT_PRIVATE_KEY("importprivkey"),
    GET_ACCOUNT("getaccount"),
    GET_ACCOUNT_ADDRESS("getaccountaddress"),
    LIST_ACCOUNTS("listaccounts"),
    LIST_ADDRESS_GROUPINGS("listaddressgroupings"),
    LIST_RECEIVED_BY_ACCOUNT("listreceivedbyaccount"),
    LIST_RECEIVED_BY_ADDRESS("listreceivedbyaddress"),
    SEND_FROM("sendfrom"),
    SEND_RAW_TRANSACTION("sendrawtransaction"),
    SET_ACCOUNT("setaccount"),
    SEND_TO_ADDRESS("sendtoaddress"),
    GET_ADDRESSES_BY_ACCOUNT("getaddressesbyaccount"),
    GET_RECEIVED_BY_ACCOUNT("getreceivedbyaccount"),
    GET_RECEIVED_BY_ADDRESS("getreceivedbyaddress"),
    GET_BALANCE("getbalance"),
    GET_TRANSACTION("gettransaction"),
    GET_CONNECTION_COUNT("getconnectioncount"),
    BACKUP_WALLET("backupwallet"),
    DECODE_RAW_TRANSACTION("decoderawtransaction"),
    GET_RAW_TRANSACTION("getrawtransaction"),
    LIST_TRANSACTIONS("listtransactions"),
    ENCRYPT_WALLET("encryptwallet"),
    MOVE("move"),
    WALLET_PASS_PHRASE("walletpassphrase"),
    ENCODE_DATA_SECURITY_EMAIL("encodedatasecurityemail"),
    DECODE_DATA_SECURITY_EMAIL("decodedatasecurityemail"),
    ENCODE_DATA_SECURITY_EMAIL_HASH("encodedatasecurityemailhash"),
    DECODE_DATA_SECURITY_EMAIL_HASH("decodedatasecurityemailhash"),
    ACCOUNT_EXISTS("accountexists"),
    ADDRESS_EXISTS("addressexists"),
    GET_PUB_KEY("getpubkey"),
    GET_PUB_KEY_OF_PRIV_KEY("getpubkeyofprivkey"),
    GET_MULTISIG_ACCOUNT_ADDRESS("getmultisigaccountaddress"),
    GET_MULTISIG_ADDRESSES_BY_ACCOUNT("getmultisigaddressesbyaccount"),
    GET_MULTISIG_ADDRESSES("getmultisigaddresses"),
    GET_MULTISIG_ADDRESS_BY_ACCOUNT("getmultisigaddressesbyaccount"),
    GET_MULTISIG_ADDRESS_OF_ADDRESS_OR_ACCOUNT("getmultisigaddressofaddressoraccount"),
    GET_BITCOIN_ADDRESS_OF_PUB_KEY("getbitcoinaddressofpubkey"),
    GET_BITCOIN_ADDRESS_OF_PRIV_KEY("getbitcoinaddressofprivkey"),
    GET_PRIV_KEY("getprivkey"),
    CREATE_MULTISIG_ADDRESS_EX("createmultisigaddressex"),
    ADD_MULTISIG_ADDRESS("addmultisigaddress"),
    ADD_MULTISIG_ADDRESS_EX("addmultisigaddressex"),
    CREATE_MULTISIG_EX("createmultisigex"),
    CREATE_AND_ADD_MULTISIG_ADDRESS_EX("createandaddmultisigaddressex"), 
    SIGN_AND_SEND_RAW_TRANSACTION_MULTISIG("signandsendrawtransaction_multisig"), 
    SIGN_RAW_TRANSACTION_MULTISIG("signrawtransaction_multisig"),
    SEND_RAW_TRANSACTION_MULTISIG("sendrawtransaction_multisig"),
    MY_OUTPUTRAWTRANSACTION("my_outputrawtransaction"),
    LIST_TRANSACTIONS_MULTISIG("listtransactions_multisig"),
    LIST_UNSPENT_MULTISIG("listunspent_multisig"),
    IS_VALID_PUB_KEY("isvalidpubkey"),
    IS_VALID_PRIV_KEY("isvalidprivkey"),
    IS_VALID_BITCOIN_ADDRESS("isvalidbitcoinaddress"),
    IS_MULTISIG_ADDRESS("ismultisigaddress"),
    IS_MINE_PUB_KEY("isminepubkey"),
    IS_MINE_PRIV_KEY("ismineprivkey"),
    IS_MINE_BITCOIN_ADDRESS("isminebitcoinaddress"),
    HAS_PUB_KEY("haspubkey"),
    HAS_PRIV_KEY("hasprivkey"),
    GET_TOTAL_CONFIRMATIONS_OF_TXIDS("gettotalconfirmationsoftxids"),
    GET_AVERAGE_CONFIRMATIONS_OF_TXIDS("getaverageconfirmationsoftxids"),
    DECODE_RAW_TRANSACTION_MULTISIG("decoderawtransaction_multisig"),
    CREATE_TRANSACTION_MULTISIG("createtransaction_multisig"),
    CREATE_RAW_TRANSACTION_MULTISIG("createrawtransaction_multisig");
    private String value;

    private APICalls(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
//        return super.toString(); //To change body of generated methods, choose Tools | Templates.
        return value;
    }

}
