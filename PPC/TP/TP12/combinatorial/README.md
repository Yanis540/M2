```
$ make runtrace_abs TRACE=1
$ make runtrace_max TRACE=1
$ make runtrace_saturate TRACE=1
```

## Model checking using `kind 2` (https://kind.cs.uiowa.edu/app)

```
node assertion(a, b : int) returns (ok:bool);
  let
    ok = (a + b) = (b + a);
    --%PROPERTY ok;
  tel;
```