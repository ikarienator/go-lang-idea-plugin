package main

type T1 []string
type T2 T1

func main(a ...string) {
    var (x T2; y T1; z []string)
    /*type*/x
    /*type*/y
    /*type*/z
    /*type*/a
}
