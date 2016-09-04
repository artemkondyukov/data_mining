/**
  * Created by fonturacetamum on 04/09/16.
  */
object Apriori extends App {
  val transactions = Map(
    1 -> List("A", "B", "C"),
    2 -> List("A", "B", "C"),
    3 -> List("B", "C"),
    4 -> List("B", "D"),
    5 -> List("B", "C", "D", "E"),
    6 -> List("E")
  )

  val min_support = 2

  def frequent_itemsets[A](transactions: Map[Int, List[A]],
                           accumulator: List[(List[A], Int)] = Nil): List[(List[A], Int)] = {

    val items = transactions.values.toList.flatten.toSet
    val acc_next = accumulator match {
      case Nil => items.map(item => List(item)).toList
      case list => accumulator.flatMap(itemset => items.map(_ :: itemset._1))
    }
    val filtered = acc_next
      .map(list => list -> transactions.count(_._2.intersect(list) == list) )
      .filter(_._2 >= min_support)

    if (filtered.isEmpty)
      return Nil
    accumulator ::: frequent_itemsets(transactions, filtered)
  }

  val itemsets = frequent_itemsets(transactions).toMap
  val confidence = itemsets(List("B", "C")).toDouble / itemsets(List("B"))

  println("Itemsets: ")
  itemsets foreach println

  println("B -> C confidence: " + confidence)
}
