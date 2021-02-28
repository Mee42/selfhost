const print = console.log;
const std_plus = (a, b) => a + b; /* todo make N-ary */
const std_minus = (a, b) => a - b;
const std_times = (a, b) => a * b;
const std_div = (a,b) => a / b;
const std_mod = (a, b) => a % b;
const std_if = (cond, a, b) => cond ? a : b;
const std_true = true;
const std_false = false;
const std_concat = (a,b) =>  { 
    let x = [];
    x = x.concat(a);
    x = x.concat(b);
    return x;
}; /* TODO work for N-ary */
const eq = (a, b) => a === b;

const not = (a) => !a;

const js = (a) => eval(a);


/* these builtins are implemented in JS for performance (mainly for non-recursive options) */

const builtin_loop = (start, end, f) => {
    for(let i = start; i < end; i++) f(i);
};

const range = (start, end) => {
    let list = [];
    for(let i = start; i < end; i++) list += i;
    return list;
};
/*
takes in: $0 a list
          $1 a function, that, for each element, takes in:
              $0 - element
              $1 - the index
              $2 - the original $0 list
          and returns the new value to use

 */
const map = (list, f) => {
    return list.map(f)
};

/* same as map, but f should return a boolean */
/* if it's true, the element is kept in the list, otherwise discarded */
const select = (list, f) => { 
    return list.filter(f)
};

/* if list is [1, 2, 3, 6] */
/* returns f(f(f(1, 2), 3), 6) */
const reduceNonEmpty = (list, f) => {
    let e = list[0];
    for(let i = 1; i < list.length; i++) {
        e = f(e, list[i]);
    }
    return e;
};
const reduceWithStarter = (list, starter, f) => {
    let e = starter;
    for(let i = 0; i < list.length; i++) {
        e = f(e, list[i])
    }
    return e;
};

/* reads the file. Assumes utf8 */
const readFile = (filename) => {
    return require('fs').readFileSync(filename, 'utf8')
};

const length = (x) => x.length;

const get = (a, b) => a[b];

const indexOf = (str, item, startingIndex) => str.indexOf(item, startingIndex);
const substring = (str, start, end) => str.substring(start, end);
const slice = (array, start, end) => array.slice(start, end);
/* this calls 'f' with 'arr' as the argument array*/
/* call { $N } (list 1 2 3) => (list 1 2 3) */
const call = (f, arr) => {
    return f(...arr);
};

/* 
$0 the input string
$1 the function to determine if a chars' index should be used
$2 the value to return in the event that none match */

const indexOfFirstOrElse = (str, f, value) => {
    for(let i = 0; i < str.length; i++) {
        if(f(str[i])) return i;
    }
    return value;
};

const CHARS_hash = '#';
const CHARS_newline = '\n';
const CHARS_quote = '"';
;