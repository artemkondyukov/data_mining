import DecisionTree.Objective.Objective

/**
  * Created by fonturacetamum on 03/09/16.
  */
object DecisionTree extends App {
  object Objective extends Enumeration {
    type Objective = Value
    val Gini, InformationGain = Value
  }

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
  def buildTree[A](data: List[(List[A], A)], objective: Objective): Map[Set[A], Any] = {
    if (data.isEmpty) throw new java.lang.IllegalArgumentException

    // How many different outcomes are and what's are their frequencies
    val outcome_counts = value_counts(data.map(_._2))
    if (outcome_counts.size == 1)
      return Map((Set.empty[A], outcome_counts.toList.head._1))

    // There are no features to build tree on
    if (data.head._1.isEmpty) return Map((Set.empty[A], outcome_counts.toList.head._1))

    // Shortcuts for probability and log2 functions
    def p(valAmount: Int) = valAmount.toDouble / data.length
    def log2(value: Double): Double = Math.log(value) / Math.log(2)

    // Reversed entropy
    val information_gains = data.head
      ._1
      .zipWithIndex
      .map(feature => value_counts(data.map(row => row._1(feature._2) -> row._2)))
      .map(_
        .foldLeft(0.0)((l, r) => l - p(1) * r._2 * log2(r._2.toDouble / outcome_counts(r._1._2)))
      )

    def sqr(x: Double): Double = { x * x }
    val gini_indices: List[Set[((Set[A], Set[A]), Double)]] = data.head
      ._1
      .zipWithIndex
      .map(feature =>
        cPowerSet(data.map(row => row._1(feature._2)).toSet).map(comb => {
          val mult = data.count(row => comb._1.contains(row._1(feature._2))).toDouble / data.size
          val multList = List(mult, 1 - mult)
          comb -> List(comb._1, comb._2)
            .map(combPart => {
              val matching = data.filter(row => combPart.contains(row._1(feature._2)))
              value_counts(matching
                .map(row => row._1(feature._2) -> row._2))
                .mapValues(count => count.toDouble / matching.size)
            }
              .foldLeft(1.0)((gini, prob) => gini - sqr(prob._2))
            )
            .zip(multList)
            .foldLeft(0.0)((gini, listGini) => gini + listGini._2 * listGini._1)
        })
      )

    val maxGiniTuple = gini_indices.zipWithIndex
      .map(set => set._1.toList.maxBy { _._2 } -> set._2)
      .max(Ordering[Double].on[((_, Double), _)](_._1._2))
    val giniSplit = maxGiniTuple._1._1 -> maxGiniTuple._2

    def dropIndex(index: Int)(list: List[A]) = list.take(index - 1) ++ list.takeRight(list.length - index - 1)
    objective match {
      case Objective.InformationGain =>
        val index = information_gains.indexOf(information_gains.max)

        val feature_subtrees = data
          .map(row => row._1(index))
          .distinct
          .map(
            value => Set(value) -> buildTree(
              data
                .filter(row => row._1(index) == value)
                .map(row => dropIndex(index)(row._1) -> row._2),
              objective
            )
          )
        feature_subtrees.toMap
      case Objective.Gini => {
        val feature_subtrees = List(giniSplit._1._1, giniSplit._1._2)
          .map(
            value => value -> buildTree(
             data
                .filter(row => value.contains(row._1(giniSplit._2)))
                .map(row => value.size match {
                  case 1 => dropIndex(giniSplit._2)(row._1) -> row._2
                  case len => row
                }),
              objective
            )
          )
        feature_subtrees.toMap
      }
    }

  }

  // TODO rewrite in functional fashion
  def printTree(tree: Map[Set[String], Any], tabs: Int = 0): Unit = {
    tree.toList.foreach(value => {
//      value._1.foreach(node => println("\t" * tabs + node))
      value._1.size match {
        case 0 =>
        case len =>  println ("\t" * tabs + value._1.mkString (", ") )
      }
      value._2 match {
        case s: String => println("\t" * tabs + s)
        case map: Map[Set[String], Any] => printTree(map, tabs+1)
      }
    })
  }

  def getRules(tree: Map[Set[String], Any]): List[(List[List[String]], Any)] = {
    tree.map(value => value._2 match {
      case string: String => (Nil -> value._2) :: Nil
      case subtree: Map[Set[String], Map[Set[String], Any]] =>
        getRules(subtree).map(rule => (value._1.toList :: rule._1) -> rule._2)
    }
    ).toList.flatten
  }

  def getRuleAccuracy(tree: Map[Set[String], Any], rule: List[List[String]]): Double = {
    val satisfying = rule
      .zipWithIndex
      .foldLeft(data)((intermediate, rulePart) =>
        intermediate.filter(row => rulePart._1.contains(row._1(rulePart._2))))
    value_counts(satisfying.map(_._2)).values
      .max
      .toDouble / satisfying.length
  }

  val decisionTreeInfoGain = buildTree(data, Objective.InformationGain)

  println("Tree: ")
  printTree(decisionTreeInfoGain)
  println("\nRules: ")
  val rules = getRules(decisionTreeInfoGain)
//  val acc = getRuleAccuracy(decisionTree, List("small", "blonde", "brown"))
  rules.foreach(rule => print(rule + ". Accuracy: " + getRuleAccuracy(decisionTreeInfoGain, rule._1) + "\n"))

  val decisionTreeGini = buildTree(data, Objective.Gini)
  println("Tree using Gini: ")
  printTree(decisionTreeGini)

}
