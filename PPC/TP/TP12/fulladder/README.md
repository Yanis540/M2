```
$ make runtrace_full_add TRACE=1
0 0 0				    0
...
$ make runtrace_full_add TRACE=2
$ make runtrace_full_add_h TRACE=1
$ make runtrace_equivalence TRACE=1
```

## Model checking using `kind 2` (https://kind.cs.uiowa.edu/app)

```
node assertion(a, b : int) returns (ok:bool);
  let
    ok = (a + b) = (b + a);
    --%PROPERTY ok;
  tel;
```