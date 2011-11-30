/* Generated By:JavaCC: Do not edit this line. Interpretador.java */
package ispd.arquivo.interpretador.gerador;

class Interpretador implements InterpretadorConstants {

        public boolean verbose;
        private String textoVerbose = "Saida do Verbose:";
        public boolean erroEncontrado = false;
        private String erros = "Erros encontrados durante o parser do Gerador:";

        public void resetaObjetosParser(){
                textoVerbose = "";
                erroEncontrado = false;
        }

        public void printv(String msg){
                textoVerbose = textoVerbose+"\u005cn>"+msg;
        }

        public void addErro(String msg){
                erros = erros+"\u005cn"+msg;
        }

        public void resuladoParser(){
                if(erroEncontrado){
                        MostraSaida saida = new MostraSaida("Found Errors",erros);
                        saida.setVisible(true);
                }
                else{
                        if(verbose){
                            MostraSaida saida = new MostraSaida("Saida do Reconhecimento",textoVerbose);
                            saida.setVisible(true);
                        }
                }
        }

        public void consomeTokens(){
                Token t = getToken(1);
                while( t.kind != SCHEDULER && t.kind != STATIC && t.kind != DYNAMIC && t.kind != TASK && t.kind != RESOURCE && t.kind != EOF){
                        getNextToken();
                        t = getToken(1);
                }
        }

  final public void Escalonador() throws ParseException {
        resetaObjetosParser();
    try {
      Partes();
      jj_consume_token(0);
                        printv("Escalonador reconhecido");

                        resuladoParser();
    } catch (ParseException e) {
                Token t = getToken(1);
                addErro("Erro semantico encontrado na linha "+t.endLine+", coluna "+t.endColumn);
                erroEncontrado = true;
                consomeTokens();
                resuladoParser();
    }
  }

  final public void Partes() throws ParseException {
    label_1:
    while (true) {
      Parte();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SCHEDULER:
      case STATIC:
      case DYNAMIC:
      case TASK:
      case RESOURCE:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
    }
                  printv("Componentes reconhecidos");
  }

  final public void Parte() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SCHEDULER:
      Nome();
             printv("Reconheceu nome do escaonador");
      break;
    case STATIC:
    case DYNAMIC:
      Caracteristica();
                       printv("Reconheceu caracteristicas");
      break;
    case TASK:
      EscalonadorTarefa();
                          printv("Reconheceu politica de escalonamento das tarefas");
      break;
    case RESOURCE:
      EscalonadorRecurso();
                           printv("Reconheceu politica de escalonamento dos recursos");
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void Nome() throws ParseException {
        Token t;
    jj_consume_token(SCHEDULER);
    t = jj_consume_token(nome);
                               printv("Reconheceu nome no escravo");
  }

  final public void Caracteristica() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STATIC:
      jj_consume_token(STATIC);
      break;
    case DYNAMIC:
      jj_consume_token(DYNAMIC);
      tipo_atualizacao();
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void tipo_atualizacao() throws ParseException {
    Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TASK:
      jj_consume_token(TASK);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ENTRY:
        jj_consume_token(ENTRY);
        break;
      case OUTPUT:
        jj_consume_token(OUTPUT);
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    case TIME:
      jj_consume_token(TIME);
      jj_consume_token(INTERVAL);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case real:
        t = jj_consume_token(real);
        break;
      case inteiro:
        t = jj_consume_token(inteiro);
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void EscalonadorTarefa() throws ParseException {
    jj_consume_token(TASK);
    jj_consume_token(SCHEDULER);
    jj_consume_token(44);
    formula();
  }

  final public void EscalonadorRecurso() throws ParseException {
    jj_consume_token(RESOURCE);
    jj_consume_token(SCHEDULER);
    jj_consume_token(44);
    formula();
  }

  final public void formula() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case RANDOM:
      jj_consume_token(RANDOM);
      break;
    case CRESCENT:
      jj_consume_token(CRESCENT);
      jj_consume_token(lparen);
      expressao();
      jj_consume_token(rparen);
      break;
    case DECREASING:
      jj_consume_token(DECREASING);
      jj_consume_token(lparen);
      expressao();
      jj_consume_token(rparen);
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void expressao() throws ParseException {
    expressao2();
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case sub:
      case add:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case add:
        jj_consume_token(add);
        break;
      case sub:
        jj_consume_token(sub);
        break;
      default:
        jj_la1[8] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      expressao2();
    }
  }

  final public void expressao2() throws ParseException {
    expressao3();
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case mult:
      case div:
        ;
        break;
      default:
        jj_la1[9] = jj_gen;
        break label_3;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case div:
        jj_consume_token(div);
        break;
      case mult:
        jj_consume_token(mult);
        break;
      default:
        jj_la1[10] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      expressao3();
    }
  }

  final public void expressao3() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case sub:
    case add:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case add:
        jj_consume_token(add);
        break;
      case sub:
        jj_consume_token(sub);
        break;
      default:
        jj_la1[11] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[12] = jj_gen;
      ;
    }
    expressao4();
  }

  final public void expressao4() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case tTamComp:
    case tTamComu:
    case tNumTarSub:
    case tNumTarConc:
    case tPoderUser:
    case rPodeProc:
    case rLinkComu:
    case rtamCompTar:
    case rtamComuTar:
    case numTarExec:
      variavel();
      break;
    case 45:
      constante();
      break;
    case lparen:
      jj_consume_token(lparen);
      expressao();
      jj_consume_token(rparen);
      break;
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void variavel() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case tTamComp:
      jj_consume_token(tTamComp);
      break;
    case tTamComu:
      jj_consume_token(tTamComu);
      break;
    case tNumTarSub:
      jj_consume_token(tNumTarSub);
      break;
    case tNumTarConc:
      jj_consume_token(tNumTarConc);
      break;
    case tPoderUser:
      jj_consume_token(tPoderUser);
      break;
    case rPodeProc:
      jj_consume_token(rPodeProc);
      break;
    case rLinkComu:
      jj_consume_token(rLinkComu);
      break;
    case rtamCompTar:
      jj_consume_token(rtamCompTar);
      break;
    case rtamComuTar:
      jj_consume_token(rtamComuTar);
      break;
    case numTarExec:
      jj_consume_token(numTarExec);
      break;
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void constante() throws ParseException {
    Token t;
    jj_consume_token(45);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case inteiro:
      t = jj_consume_token(inteiro);
      break;
    case real:
      t = jj_consume_token(real);
      break;
    default:
      jj_la1[15] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(46);
  }

  /** Generated Token Manager. */
  public InterpretadorTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[16];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x101e,0x101e,0xc,0x60,0x60000000,0x90,0xe00,0x6000000,0x6000000,0x1800000,0x1800000,0x6000000,0x6000000,0x87fe000,0x7fe000,0x60000000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x2000,0x0,0x0,};
   }

  /** Constructor with InputStream. */
  public Interpretador(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Interpretador(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new InterpretadorTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public Interpretador(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new InterpretadorTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public Interpretador(InterpretadorTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(InterpretadorTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[47];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 16; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 47; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
