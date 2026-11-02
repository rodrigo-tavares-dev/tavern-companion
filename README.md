# Tavern Companion 🎲⚔️

**Aplicativo Android nativo para rastreamento de combate e rolagem de dados de RPG de mesa.**

---

## 📋 Informações do Aluno e Entrega

* **Aluno:** Rodrigo Tavares Vieira
* **Data de Entrega:** 02 de novembro de 2026
* **Projeto:** Avaliação Prática de Desenvolvimento Android

---

## 💡 Justificativa da Escolha do Tema

Em sessões presenciais de jogos de RPG (como *Dungeons & Dragons*, *Tormenta20* e sistemas afins), o mestre e os jogadores frequentemente enfrentam sobrecarga cognitiva ao registrar manualmente no papel a ordem de iniciativa de dezenas de combatentes, seus pontos de vida (HP) dinâmicos e eventuais condições de combate, além de demandar múltiplos conjuntos de dados físicos.

O **Tavern Companion** foi idealizado para reunir em um único ecossistema mobile leve e intuitivo as duas ferramentas essenciais do "Escudo do Mestre":
1. Um **rastreador ágil de combate** que ordena automaticamente a vez de ação de aliados e inimigos e mantém os dados persistentes mesmo que o aplicativo seja fechado.
2. Uma **mesa de dados poliédricos imersiva** que aproveita as capacidades de hardware do dispositivo móvel (acelerômetro físico e feedback sonoro) para reproduzir a sensação tátil e auditiva de rolar dados na mesa sem precisar de botões complexos.

O tema permitiu aplicar de ponta a ponta todos os requisitos arquiteturais e funcionais exigidos na formação, integrando persistência local, regras de negócio reativas e sensores nativos do sistema operacional Android.

---

## ⚙️ Descrição e Funcionamento do Aplicativo

O aplicativo é dividido em duas telas principais interligadas via navegação declarativa:

### 1. Rastreador de Combate (Iniciativa & HP)
* **Cadastro Dinâmico:** Permite adicionar participantes ao combate definindo Nome, Iniciativa, Pontos de Vida (HP) e Classificação (Jogador ou Monstro/Inimigo).
* **Ordenação Reativa:** A lista organiza os combatentes automaticamente em ordem decrescente de Iniciativa.
* **Gestão de Dano/Cura em Tempo Real:** Botões dedicados de incremento (`+`) e decremento (`-`) ajustam o HP instantaneamente.
* **Feedback Visual de Estado:**
  * Jogadores e Inimigos possuem identificação visual distinta através de bordas temáticas (azul para aliados, vermelho para monstros).
  * Quando o HP de um participante atinge zero, o card exibe o status de "Inconsciente/Derrotado", facilitando a leitura rápida da batalha.
* **Persistência Local Completa:** Todos os combatentes e seus estados são gravados em banco de dados SQLite local gerenciado via **Room Database**, garantindo que nenhum progresso seja perdido caso o app saia de memória.

### 2. Mesa de Rolagem de Dados Poliédricos
* **Variedade de Dados:** Suporte aos dados padrão de RPG: `D4`, `D6`, `D8`, `D10`, `D12`, `D20` e `D100`.
* **Detecção de Movimento (Acelerômetro):** O usuário pode selecionar o dado e simplesmente **chacoalhar o smartphone** para efetuar a rolagem física, graças ao algoritmo de detecção vetorial implementado com `ShakeDetector`.
* **Disparo Sonoro:** Ao rolar o dado (seja por gesto ou toque), um efeito sonoro de dados colidindo na mesa é reproduzido nativamente através do `MediaPlayer`.
* **Regras Especiais do D20:** O sistema detecta automaticamente e destaca visualmente rolagens críticas:
  * **20 Natural:** Sucesso Crítico com destaque dourado/verde.
  * **1 Natural:** Falha Crítica com alerta visual vermelho.

---

## 🏗️ Arquitetura e Tecnologias Utilizadas

* **Linguagem:** Kotlin
* **Interface Gráfica:** Jetpack Compose com componentes modernos Material 3.
* **Padrão Arquitetural:** MVVM (Model-View-ViewModel), promovendo total desacoplamento entre lógica de negócio, camada de dados e UI.
* **Persistência de Dados:** Room Database (`TavernDatabase`, `CombatantDao`, `CombatantRepository`) com persistência assíncrona.
* **Programação Assíncrona e Reatividade:** Kotlin Coroutines e `StateFlow`.
* **Navegação:** AndroidX Navigation Compose (`NavHost` e rotas tipadas).
* **Acesso a Hardware / Multimídia:**
  * `SensorManager` e `SensorEventListener` (leitura vetorial do acelerômetro com filtro anti-ruído).
  * `MediaPlayer` consumindo recursos nativos de áudio em `res/raw`.

---

## 📌 Observações Relevantes do Aluno

* **Versionamento Semântico:** Todo o fluxo de desenvolvimento do projeto seguiu rigorosamente as convenções de commits semânticos (*Conventional Commits*: `feat`, `fix`, etc.), mantendo um histórico auditável e profissional no GitHub.
* **Código Documentado em Português:** As funções críticas de negócio, inicializações de hardware e factories do ViewModel receberam comentários técnicos detalhados em língua portuguesa, facilitando a revisão pedagógica.
* **Resiliência a Recursos Ausentes:** A integração do `MediaPlayer` foi arquitetada de forma segura (bloco `try-catch` com liberação explícita de memória via `it.release()`), garantindo que o aplicativo nunca sofra *crashes* mesmo se houver falhas no subsistema de áudio.
* **Eliminação de Dependências Desnecessárias:** O projeto priorizou as APIs nativas do Android e as bibliotecas oficiais do Jetpack, mantendo a compilação enxuta e tempo de resposta otimizado.