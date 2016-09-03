/**
  * Created by fonturacetamum on 03/09/16.
  */
object DecisionTree extends App {
  val data = List(
    (List("small", "blonde", "brown"), "no"),
    (List("tall",  "dark",   "brown"), "no"),
    (List("tall",  "blonde", "blue"),  "yes"),
    (List("tall",  "dark",   "blue"),  "no"),
    (List("small", "dark",   "blue"),  "no"),
    (List("tall",  "red",    "blue"),  "yes"),
    (List("tall",  "blonde", "brown"), "no"),
    (List("small", "blonde", "blue"),  "yes")
  )

  def value_counts[A](data: List[A]): Map[A, Int] = {
    data.distinct.map(value => value -> data.count(_ == value)).toMap
  }

  @annotation.tailrec
  def powerSet[A](set: Set[A], accumulator: Set[Set[A]] = Set(Set.empty[A])): Set[Set[A]] = {
    if (set.isEmpty) accumulator
    else powerSet(set.tail, accumulator ++ accumulator.map(_ + set.head))
  }

  // Does not contain perfect and empty sets, splits sets into complementary pairs
  def cPowerSet[A](set: Set[A]): Set[(Set[A], Set[A])] = {
    val filtered = powerSet(set) - Set.empty[A] - set
    filtered.map(part => Set(part, set -- part)).map(s => (s.toList.head, s.toList(1)))
  }

  // By now is returns Map[A, Any] where Any represents another map
  // TODO reimplement it using Tree class
  def buildTree[A](data: List[(List[A], A)]): Map[Option[A], Any] = {
    if (data.isEmpty) throw new java.lang.IllegalArgumentException

    // How many different outcomes are and what's are their frequences
    val outcome_counts = value_counts(data.map(_._2))
    if (outcome_counts.size == 1)
      return Map((None, outcome_counts.toList.head._1))

    // There are no features to build tree on
    if (data.head._1.isEmpty) return Map((None, outcome_counts.toList.head._1))

    // Shortcuts for probability and log2 functions
    def p(valAmount: Int) = valAmount.toDouble / data.length
    def log2(value: Double): Double = Math.log(value) / Math.log(2)

    // Reversed entropy
    val information = outcome_counts.foldLeft(0.0)((l, r) => l - p(r._2)*log2(p(r._2)))
    val information_gains = data.head
      ._1
      .zipWithIndex
      .map(feature => value_counts(data.map(row => row._1(feature._2) -> row._2)))
      .map(_
        .foldLeft(0.0)((l, r) => l - p(1) * r._2 * log2(r._2.toDouble / outcome_counts(r._1._2)))
      )

//      val gini_indices = data(0)
//        ._1
//        .zipWithIndex
//        .map(feature =>
//          rPowerSet(data.map(row => row._1(feature._2)).toSet).map(comb =>
//
//          )
//        )


    val index = information_gains.indexOf(information_gains.max)
    def dropIndex(list: List[A]) = list.take(index - 1) ++ list.takeRight(list.length - index - 1)

    val feature_subtrees = data
      .map(row => row._1(index))
      .distinct
      .map(
        value => Some(value) -> buildTree(
          data
            .filter(row => row._1(index) == value)
            .map(row => dropIndex(row._1) -> row._2)
        )
      )
    feature_subtrees.toMap
  }

  // TODO rewrite in functional fashion
  def printTree(tree: Map[Option[String], Any], tabs: Int = 0): Unit = {
    tree.toList.foreach(value => {
      value._1.foreach(node => println("\t" * tabs + node))
      value._2 match {
        case s: String => println("\t" * tabs + s)
        case map: Map[Option[String], Any] => printTree(map, tabs+1)
      }
    })
  }

  def getRules(tree: Map[Option[String], Any]): List[(List[String], Any)] = {
    tree.map(value => value._2 match {
      case string: String => (Nil -> value._2) :: Nil
      case subtree: Map[Option[String], Map[Option[String], Any]] => {
        getRules(subtree).map(rule => (value._1.getOrElse("") :: rule._1) -> rule._2)
      }
    }
    ).toList.flatten
  }

  def getRuleAccuracy(tree: Map[Option[String], Any], rule: List[String]): Double = {
    val satisfying = rule
      .zipWithIndex
      .foldLeft(data)((intermediate, rulePart) =>
        intermediate.filter(row => row._1(rulePart._2) == rulePart._1))
    value_counts(satisfying.map(_._2)).values
      .max
      .toDouble / satisfying.length
  }

  println(cPowerSet(Set(1, 2, 3)))
}
