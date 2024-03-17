package transferenciadadostp;

import java.util.Arrays;
import java.util.Random;

public class Transmissor {
    private String mensagem;
    //polimonio gerador = 1011000110
    public static final boolean[] polimonio = {true, false, true, true, false, false, false, true, true, false};

    public Transmissor(String mensagem) {
        this.mensagem = mensagem;
    }
    
    //convertendo um símbolo para "vetor" de boolean (bits)
    private boolean[] streamCaracter(char simbolo){
        
        //cada símbolo da tabela ASCII é representado com 8 bits
        boolean bits[] = new boolean[8];
        
        //convertendo um char para int (encontramos o valor do mesmo na tabela ASCII)
        int valorSimbolo = (int) simbolo;
        int indice = 7;
        
        //convertendo cada "bits" do valor da tabela ASCII
        while(valorSimbolo >= 2){
            int resto = valorSimbolo % 2;
            valorSimbolo /= 2;
            bits[indice] = (resto == 1);
            indice--;
        }
        bits[indice] = (valorSimbolo == 1);
        
        return bits;
    } 
    
    //não modifique (seu objetivo é corrigir esse erro gerado no receptor)
    private void geradorRuido(boolean bits[]){
        Random geradorAleatorio = new Random();
        
        //pode gerar um erro ou não..
        if(geradorAleatorio.nextInt(5) > 1){
            int indice = geradorAleatorio.nextInt(8);
            bits[indice] = !bits[indice];
        }
    }

    //remove os zeros a esquerda de um binario e retorna um novo vetor
    public boolean[] getBinarioSemZerosEsquerda(boolean[] bits) {
        int posicaoInicial = 0;
        for (int indice = 0; indice < bits.length; indice++) {
            if (bits[indice]) {
                posicaoInicial = indice;
                break;
            }
        }
        return Arrays.copyOfRange(bits, posicaoInicial, bits.length);

    }

    //codifica bits com CRC
    private boolean[] dadoBitsCRC(boolean[] bitsOriginais) {
        //remove possiveis zeros a esquerda do binario
        bitsOriginais = getBinarioSemZerosEsquerda(bitsOriginais);

        boolean[] bitsComZeros = new boolean[bitsOriginais.length + (polimonio.length - 1)];
        //armazena o dado orignal com os zeros adicionais em um novo vetor
        for (int bit = 0; bit < bitsComZeros.length; bit++) {
            if (bit < bitsOriginais.length)
                bitsComZeros[bit] = bitsOriginais[bit];
            else
                bitsComZeros[bit] = false;
        }

        int posicaoFinal = polimonio.length - 1;
        boolean[] resto = new boolean[polimonio.length];

        //bits que serao usados na operacao XOR
        boolean[] bitsXOR = Arrays.copyOfRange(bitsComZeros, 0, polimonio.length);

        while (posicaoFinal < bitsComZeros.length) {

            //primeiro bit = 1
            if (bitsXOR[0]) {
                //Operacao XOR, armazenando o resultado no resto
                for (int indice = 0; indice < polimonio.length; indice++) {
                    resto[indice] = bitsXOR[indice] != polimonio[indice];
                }
            }
            //primeiro bit = 0
            else {
                for (int indice = 0; indice < polimonio.length; indice++) {
                    resto[indice] = bitsXOR[indice];
                }
            }
            //primeiro bit do resto = 0
            if (!resto[0]) {
                if (posicaoFinal < bitsComZeros.length - 1){
                    for (int indice = 0; indice < resto.length - 1; indice++) {
                        resto[indice] = resto[indice + 1];
                    }


                    posicaoFinal++;
                    resto[resto.length - 1] = bitsComZeros[posicaoFinal];
                }
                else break;
            }
            bitsXOR = Arrays.copyOf(resto, resto.length);
        }

        boolean[] bitsCRC = Arrays.copyOf(bitsComZeros, bitsComZeros.length);
        int ultimaPosicaoResto;
        int ultimaPosicaoBitsCRC;

        //adiciona resto aos bits originais, gerando os bits codificados
        for (int indice = 0; indice < polimonio.length - 1; indice++) {
            ultimaPosicaoResto = resto.length - 1 - indice;
            ultimaPosicaoBitsCRC = bitsCRC.length - 1 - indice;
            bitsCRC[ultimaPosicaoBitsCRC] = resto[ultimaPosicaoResto];

        }
        return bitsCRC;
    }
    
    public void enviaDado(Receptor receptor){
        for(int i = 0; i < this.mensagem.length();i++){
            boolean bits[] = streamCaracter(this.mensagem.charAt(i));
            boolean indicadorCRC = false;

            while (!indicadorCRC) {
                //codifica bits
                boolean bitsCRC[] = dadoBitsCRC(bits);
                //add ruidos na mensagem a ser enviada para o receptor
                geradorRuido(bitsCRC);
                //enviando a mensagem "pela rede" para o receptor (uma forma de testarmos esse método)
                indicadorCRC = receptor.receberDadoBits(bitsCRC);
            }
        }
    }
}
