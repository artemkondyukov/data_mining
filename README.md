## Assignment 1 for the Data Mining course
This is the implementation of _DecisionTree_ builder using either Gini index or information gain as splitting criteria.

Using sample data _IG_ criterion gives the following tree:
root
	small
		blonde
			brown
				no
			blue
				yes
		dark
			no
	tall
		dark
			no
		blonde
			blue
				yes
			brown
				no
		red
			yes

And the following *rules*:
(List(List(small), List(blonde), List(brown),no). Accuracy: 1.0
(List(List(small), List(blonde), List(blue)),yes). Accuracy: 1.0
(List(List(small), List(dark)),no). Accuracy: 1.0
(List(List(tall), List(dark)),no). Accuracy: 1.0
(List(List(tall), List(blonde), List(blue)),yes). Accuracy: 1.0
(List(List(tall), List(blonde), List(brown)),no). Accuracy: 1.0
(List(List(tall), List(red)),yes). Accuracy: 1.0


Using sample data _Gini_ criterion gives the following tree:
root
	red
		yes
	blonde, dark
		small
			brown
				no
			blue
				no
		tall
			dark
				no
			blonde
				blue
					yes
				brown
					no
