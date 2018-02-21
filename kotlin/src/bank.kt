
fun main(args: Array<String>) {
    val account = CheckingAccount("1234", Amount(20.0f), listOf(), Customer("toto"))
    val (debitedAccount, transaction) =
            withdraw(Amount(8.0f), WithdrawableAccount.WithdrawableCheckingAccount(account))
    println("transaction amount ${transaction.amount}")

    val balance = when(debitedAccount) {
        is WithdrawableAccount.WithdrawableCheckingAccount -> debitedAccount.account.balance
        is WithdrawableAccount.WithdrawableTradingAccount -> debitedAccount.account.solde
    }
    val lastTransaction = when(debitedAccount) {
        is WithdrawableAccount.WithdrawableCheckingAccount -> debitedAccount.account.history.last()
        is WithdrawableAccount.WithdrawableTradingAccount -> debitedAccount.account.history.last()
    }
    println("account balance $balance")
    println("account last transaction $lastTransaction")
}

fun withdraw(amount: Amount, accountToDebit: WithdrawableAccount): WithdrawResult {
    val debitedAccount: WithdrawableAccount = when(accountToDebit) {
        is WithdrawableAccount.WithdrawableCheckingAccount -> {
            val account = debitCheckingAccount(accountToDebit.account, amount)
            WithdrawableAccount.WithdrawableCheckingAccount(account)
        }
        is WithdrawableAccount.WithdrawableTradingAccount -> accountToDebit
    }
    return WithdrawResult(debitedAccount, Transaction.Debit(amount))
}

data class Amount(val amount: Float)

sealed class WithdrawableAccount {
    data class WithdrawableCheckingAccount(val account: CheckingAccount): WithdrawableAccount()
    data class WithdrawableTradingAccount(val account: TradingAccount): WithdrawableAccount()
}

data class CheckingAccount(val iban: String, val balance: Amount, val history: History, val owner: Customer)

fun debitCheckingAccount(account: CheckingAccount, amount: Amount) =
        CheckingAccount(
                account.iban,
                Amount(account.balance.amount - amount.amount),
                account.history.plus(Transaction.Debit(amount)),
                account.owner)

data class TradingAccount(val solde: Amount, val history: History, val owner: Customer)

typealias History = List<Transaction>

data class WithdrawResult(val debitedAccount: WithdrawableAccount, val transaction: Transaction.Debit)

sealed class Transaction() {
    data class Debit(val amount: Amount): Transaction()
    data class Credit(val amount: Amount): Transaction()
}

data class Customer(val name: String)
