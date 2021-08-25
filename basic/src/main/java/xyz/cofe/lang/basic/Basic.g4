grammar Basic;

// Заголовок парсера
//@parser::header
//{
//import xyz.cofe.lang.basic.nodes.*;
//}

// Добавления в тело класса парсера
//@parser::members
//{
//private NodeFactory factory = new NodeFactory();
//}

r   : expr;

// Определение функции
function
    : 'fn' name=ID args fnReturn
    '{' statement ( ';' statement )* ';'? '}'
    ;

// аргументы функции
args
    : '(' arg ( ',' arg )* ')'
    ;

// аргумент
arg : name=ID ':' type=ID ;

// возвращаемый тип из функции
fnReturn
    : ':' type=ID
    ;

// утверждение - кусок кода
statement
    :   returnStatement
    ;

// возврат из функции
returnStatement
    : RETURN expr
    ;

// выражение
expr
     : left=expr op='|'       right=expr # BinOp
     | left=expr op='&'       right=expr # BinOp
     | left=expr op=('<'|'>'|'=='|'!='|'<='|'>=') right=expr # BinOp
     | left=expr op=('*'|'/') right=expr # BinOp
     | left=expr op=('+'|'-') right=expr # BinOp
     | atom                    # AtomValue
     | '(' expr ')'            # Parentheses
     | op=('-'|'+'|'!') expr   # UnaryOp
     ;

// атомарное значение
// Может относится к следующим видам
// 1. literal - литеральное значение
// 2. literal objPostFix - чтение поля
// 3. literal objPostFix callArgs - вызов метода объекта
// 4. ID - ссылка на переменную
// 5. ID callArgs - вызов метода
// 6. ID objPostFix - чтение поля
// 7. ID objPostFix  callArgs - вызов метода объекта
//
//atom : literal objPostFix ? // литерал / доступ к полю / вызов метода
//     | ID ( callArgs | objPostFix )? // вызов функции / доступ к полю / вызов метода
//     ;

atom : literal objPostFix # LiteralObj
     | literal            # LiteralValue
     | ID callArgs        # CallFun
     | ID objPostFix      # ObjAccess
     | ID                 # VarRef
     ;

// доступ к полю / вызов метода
objPostFix  : '.' ID callArgs?
            ;

// Методы аргумента
callArgs : '(' ( expr (',' expr)* )? ')';

// Литеральное значение
literal : NUMBER | STRING;

// Части идентификатора
fragment
NameChar
   : NameStartChar
   | '0'..'9'
   | '_'
   | '\u00B7'
   | '\u0300'..'\u036F'
   | '\u203F'..'\u2040'
   ;
fragment
NameStartChar
   : 'A'..'Z' | 'a'..'z'
   | '\u00C0'..'\u00D6'
   | '\u00D8'..'\u00F6'
   | '\u00F8'..'\u02FF'
   | '\u0370'..'\u037D'
   | '\u037F'..'\u1FFF'
   | '\u200C'..'\u200D'
   | '\u2070'..'\u218F'
   | '\u2C00'..'\u2FEF'
   | '\u3001'..'\uD7FF'
   | '\uF900'..'\uFDCF'
   | '\uFDF0'..'\uFFFD'
   ;

RETURN : 'return' ;

// Идентификатор
ID : NameStartChar NameChar* ;

// Число
fragment HEX_DIGIT : [0-9a-fA-F];
fragment DIGIT : [0-9] ;
NUMBER : DIGIT+ ('.' DIGIT+)? ;

// Строка
fragment ESC : '\\"' | '\\\\' | '\\\'' | '\\r' | '\\n' | '\\t' | '\\x' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT;
STRING  : '"'  ( ESC | ~[\\"\r\n] )* '"'
        | '\'' ( ESC | ~[\\"\r\n] )* '\''
        ;

// Всякие пробелы
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
