package transferenciadadostp;

import java.util.Arrays;
import static transferenciadadostp.Transmissor.polimonio;

public class Receptor {
    
    //mensagem recebida pelo transmissor
    private String mensagem;

    public Receptor() {
        //mensagem vazia no inicio da execução
        this.mensagem = "";
    }
    
    public String getMensagem() {
        return mensagem;
    }
 
    private boolean decodificarDado(boolean bits[]){
        int codigoAscii = 0;
        int expoente = bits.length-1;
        
        //converntendo os "bits" para valor inteiro para então encontrar o valor tabela ASCII
        for(int i = 0; i < bits.length;i++){
            if(bits[i]){
                codigoAscii += Math.pow(2, expoente);
            }
            expoente--;
        }
        
        //concatenando cada simbolo na mensagem original
        this.mensagem += (char)codigoAscii;
        
        return true;
    }

    //decodifica bits de CRC, retornando true se estiverem sem erros
    private boolean decodificarDadoCRC(boolean[] bitsOriginais){

        int posicaoFinal = polimonio.length - 1;

        boolean[] resto = new boolean[polimonio.length];

        //bits que serao usados na operacao XOR
        boolean[] bitsXOR = Arrays.copyOfRange(bitsOriginais, 0, polimonio.length);

        while (posicaoFinal < bitsOriginais.length) {

            //primeiro bit = 1
            if (bitsXOR[0]) {
                //Operacao XOR, armazenando o resultado no resto
                for (int indice = 0; indice < polimonio.length; indice++) {
                    resto[indice] = bitsXOR[indice] != polimonio[indice];
                }
            }
            //primeiro bit = 0
            else {
                //XOR com zeros
                for (int indice = 0; indice < polimonio.length; indice++) {
                    resto[indice] = bitsXOR[indice];
                }
            }
            //primeiro bit do resto = 0
            if (!resto[0]) {
                if (posicaoFinal < bitsOriginais.length - 1){
                    for (int indice = 0; indice < resto.length - 1; indice++) {
                        resto[indice] = resto[indice + 1];
                    }


                    posicaoFinal++;
                    resto[resto.length - 1] = bitsOriginais[posicaoFinal];
                }
                else break;
            }
            bitsXOR = Arrays.copyOf(resto, resto.length);
        }

        for (int indice = 0; indice < resto.length; indice++) {
            if (resto[indice])
                return false;
        }
        return true;
        
    }

    private boolean[] removeCRC(boolean[] bits) {
        bits = Arrays.copyOfRange(bits, 0, bits.length - polimonio.length + 1);
        if (bits.length < 8) {
            boolean[] bitsConvertidos = new boolean[8];
            int zerosParaAdicionar = 8 - bits.length;
            for (int indice = 0; indice < 8; indice++) {
                if (indice < zerosParaAdicionar)
                    bitsConvertidos[indice] = false;
                else
                    bitsConvertidos[indice] = bits[indice - zerosParaAdicionar];
            }
            bits = Arrays.copyOf(bitsConvertidos, bitsConvertidos.length);
        }
        return bits;
    }
    //recebe os dados do transmissor
    public boolean receberDadoBits(boolean bits[]){
        
        //aqui você deve trocar o médodo decofificarDado para decoficarDadoCRC (implemente!!)
        if (decodificarDadoCRC(bits)) {
            bits = removeCRC(bits);
            decodificarDado(bits);
            return true;
        }

        //será que sempre teremos sucesso nessa recepção
        return false;
    }
    
}
