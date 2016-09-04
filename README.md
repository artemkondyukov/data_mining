## Assignment 1 for the Data Mining course
src/Apriori.scala is the first two tasks of the assignment  
It produces the following output:  
Itemsets:   
(List(B),5)  
(List(A, C),2)  
(List(D),2)  
(List(B, C),4)  
(List(C),4)  
(List(B, D),2)  
(List(E),2)  
(List(A),2)  
(List(A, B),2)  
(List(A, B, C), 2)  
B -> C confidence: 0.8  

src/DecisionTree.scala is the implementation of _DecisionTree_ builder using either Gini index or information gain as splitting criteria.

Using sample data _IG_ criterion gives the following tree:  
root  
&nbsp;&nbsp;&nbsp;&nbsp;blonde  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;brown  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;no  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;blue  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;yes  
&nbsp;&nbsp;&nbsp;&nbsp;dark  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;no  
&nbsp;&nbsp;&nbsp;&nbsp;red   
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;yes

And the following *rules*:
* (List(List(blonde), List(brown),no). Accuracy: 1.0
* (List(List(blonde), List(blue),yes). Accuracy: 1.0
* (List(List(red)),yes). Accuracy: 1.0
* (List(List(dark)),no). Accuracy: 1.0


Using sample data _Gini_ criterion gives the following tree:  
root  
&nbsp;&nbsp;&nbsp;&nbsp;List(blue)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List(dark)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;no  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List(red, blonde)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;yes  
&nbsp;&nbsp;&nbsp;&nbsp;List(brown)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;no  
