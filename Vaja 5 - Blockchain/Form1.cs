using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Security.Cryptography;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace Blockchain
{
    public partial class Form1 : Form
    {
        private const string IP = "127.0.0.1";
        private const double BLOCK_GENERATION_TIME = 10;
        private const int DIFF_ADJUST_TIME = 10;
        private const double EXPECTED_TIME = BLOCK_GENERATION_TIME * DIFF_ADJUST_TIME;

        private int CurrentDifficulty = 3;

        private List<int> ConnectedClientsList = new List<int>();
        private List<Block> BlockChain = new List<Block>();

        public class Block
        {
            public int BlockIndex { get; set; }
            public string Data { get; set; }
            public DateTime TimeStamp { get; set; }
            public string PreviousHash { get; set; }
            public int BlockDifficulty { get; set; }
            public int Token { get; set; }
            public string Miner { get; set; }
            public string HashValue { get; set; }

            public override string ToString()
            {
                return "Index: " + BlockIndex + "\nData: " + Data + "\nTimestamp: " + TimeStamp.ToString() +
                       "\nPrevious Hash: " + PreviousHash + "\nDifficulty: " + BlockDifficulty +
                       "\nToken: " + Token + "\nMiner: " + Miner + "\nHash: " + HashValue + "\n";
            }
        }

        public Form1()
        {
            InitializeComponent();
        }


        // MINING
        private void StartMining()
        {
            while (true)
            {
                List<Block> newBlockChain = JsonConvert.DeserializeObject<List<Block>>(JsonConvert.SerializeObject(BlockChain)); // Creates a copy of a blockchain
                Block block = CreateBlock();    //Creates a new block

                bool blockFound = false; // Flag to indicate when a valid block is found
                object lockObj = new object(); // Lock for thread-safe operations

                Parallel.For(0, Environment.ProcessorCount, (core, state) =>
                {
                    while (!blockFound)
                    {
                        block.Token++;
                        block.HashValue = CalculateHashSHA256(block);

                        // Check if the hash meets the difficulty requirements
                        bool isValid = true;
                        for (int i = 0; i < block.BlockDifficulty; i++)
                        {
                            if (block.HashValue[i] != '0')
                            {
                                isValid = false;
                                break;
                            }
                        }

                        if (isValid)
                        {
                            lock (lockObj) // Ensure only one thread updates the blockchain
                            {
                                if (!blockFound)
                                {
                                    blockFound = true;
                                    block.Miner = $"Thread {core}"; // Assign the mining thread to the block
                                    newBlockChain.Add(block);

                                    // Output the valid block along with thread info
                                    richTextBox_blocks.Invoke(new Action(() =>
                                    {
                                        richTextBox_blocks.Select(richTextBox_blocks.TextLength, 0);
                                        richTextBox_blocks.SelectionColor = Color.Green;
                                        richTextBox_blocks.AppendText(
                                            $"Thread {core}: {block.HashValue.ToUpper()} \ndiff: {block.BlockDifficulty}\n");
                                    }));

                                    // Signal other threads to stop
                                    state.Stop();
                                }
                            }
                        }
                        else
                        {
                            // Log invalid blocks (red) periodically along with the thread ID
                            if (block.Token % 500000 == 0)
                            {
                                richTextBox_blocks.Invoke(new Action(() =>
                                {
                                    richTextBox_blocks.Select(richTextBox_blocks.TextLength, 0);
                                    richTextBox_blocks.SelectionColor = Color.Red;
                                    richTextBox_blocks.AppendText(
                                        $"Thread {core}: {block.HashValue.ToUpper()} \ndiff: {block.BlockDifficulty}\n\n");
                                }));
                            }
                        }
                    }
                });

                // Validate and update the blockchain
                ManageBlockChain(newBlockChain);
            }
        }

        //KREIRANJE BLOKA
        private Block CreateBlock()
        {
            Block block = new Block
            {
                BlockIndex = BlockChain.Count,
                Miner = textBox_username.Text,
                Data = "Blok : " + BlockChain.Count,
                TimeStamp = DateTime.Now
            };

            if (BlockChain.Count > 0) //if the blockchain already contains a block, chech if the prevhash and hash of the prev block are the same
                block.PreviousHash = BlockChain[BlockChain.Count - 1].HashValue;
            else
                block.PreviousHash = "0"; //if the generated block is the starting block, it gets prevhash value of 0

            block.BlockDifficulty = CurrentDifficulty; //block gets the difficulty of the starting difficulty
            block.Token = 0; //token of each block is at the start set to 0

            return block;
        }

        private void ManageBlockChain(List<Block> newBlockChain)
        {
            if (Validate(newBlockChain) && CompareBlockChain(newBlockChain))
            {
                BlockChain = JsonConvert.DeserializeObject<List<Block>>(JsonConvert.SerializeObject(newBlockChain)); // Update the blockchain
            }
        }

        //VALIDACIJA BLOKCHAINA
        //method for comparing blockchains, chooses a blockchain with greater cumulative difficulty
        private bool CompareBlockChain(List<Block> newBlockChain)
        {
            double cumulativeDifficultyNew = CalculateCumulativeDifficulty(newBlockChain); //zracuna kumulativnu tezavnost novog in trenutnega blockchaina
            double cumulativeDifficultyCurrent = CalculateCumulativeDifficulty(BlockChain);

            if (cumulativeDifficultyNew > cumulativeDifficultyCurrent) //ce je kumulativna tezavnost novog vecja od kumulativne tezavnosti trenutneg, nova veriga postaje glavna veriga
            {
                int numNewBlocks = newBlockChain.Count - BlockChain.Count;
                int numBlocks = BlockChain.Count;
                BlockChain = JsonConvert.DeserializeObject<List<Block>>(JsonConvert.SerializeObject(newBlockChain));

                for (int i = 1; i <= numNewBlocks; i++)
                {
                    if ((numBlocks + i) % DIFF_ADJUST_TIME == 0) //tezavnost se menja na vsakih 10 blokov
                    {
                        AdjustCurrentDifficulty();
                        break;
                    }
                }

                //Broadcasts new blockchain
                List<int> failedConnections = new List<int>();

                foreach (int port in ConnectedClientsList)
                {
                    try
                    {
                        TcpClient tcpClient = new TcpClient(IP, port);
                        Send(tcpClient.GetStream(), newBlockChain);
                        tcpClient.Close();
                    }
                    catch (Exception ex)
                    {
                        failedConnections.Add(port);
                    }
                }

                foreach (int port in failedConnections)
                {
                    ConnectedClientsList.Remove(port);
                }

                if (failedConnections.Count > 0)
                {
                    UpdateConnectedPortsTextBox();
                }

                // Updates Block Ledger TextBox
                richTextBox_chain.Invoke(new Action(() =>
                {
                    richTextBox_chain.Clear();
                    foreach (Block block in BlockChain)
                    {
                        richTextBox_chain.AppendText("\n\n" + block.ToString());
                    }
                }));

                return true;
            }
            else
            {
                return false;
            }
        }
        
        //HASHIRA 
        //Calculates hash value with sha256, taking into account difficulty and token
        private string CalculateHashSHA256(Block block)
        {
            string str = block.BlockIndex + block.Data + block.TimeStamp.ToString() + block.PreviousHash + block.BlockDifficulty + block.Token;
            StringBuilder sb = new StringBuilder();

            using (SHA256 hash = SHA256Managed.Create())
            {
                Encoding enc = Encoding.UTF8;
                byte[] result = hash.ComputeHash(enc.GetBytes(str));

                foreach (byte b in result)
                {
                    sb.Append(b.ToString("x2"));
                }
            }

            return sb.ToString();
        }


        //TRENUTNA TEZAVNOST
        //Method for calculating currect difficulty
        private void AdjustCurrentDifficulty()
        {
            Block lastBlock = BlockChain[BlockChain.Count - 1]; // Last mined block
            Block previousAdjustmentBlock = BlockChain[BlockChain.Count - DIFF_ADJUST_TIME]; // Block 10 steps ago
            double timeTaken = (lastBlock.TimeStamp - previousAdjustmentBlock.TimeStamp).TotalSeconds; // Time taken for last 10 blocks

            if (timeTaken > (EXPECTED_TIME * 2))
            {
                CurrentDifficulty--; // Decrease difficulty
            }
            else if (timeTaken < (EXPECTED_TIME / 2)) // Faster mining (under expected time for 10 blocks)
            {
                CurrentDifficulty++; // Increase difficulty
            }
        }

        //VALIDACIJA HASHOVA I CASOVNIH ZNACK
        private bool Validate(List<Block> newBlockChain)
        {
            if (newBlockChain[0].HashValue != CalculateHashSHA256(newBlockChain[0]) || //checks for genesis blok
                newBlockChain[0].PreviousHash != "0" ||
                (newBlockChain[0].TimeStamp - DateTime.Now).TotalMinutes > 1)
                return false;

            for (int i = 1; i <= newBlockChain.Count - 1; i++)
            {
                if (newBlockChain[i].HashValue != CalculateHashSHA256(newBlockChain[i]) || //checks hash values
                    newBlockChain[i].PreviousHash != newBlockChain[i - 1].HashValue || //checks current prevhash and prev block hash
                    (newBlockChain[i].TimeStamp - DateTime.Now).TotalMinutes > 1 || //blok je ustrezen, ce je njegova cas. znacka najvec 1 minuto vecja od nasega trenutnega casa
                    (newBlockChain[i - 1].TimeStamp - newBlockChain[i].TimeStamp).TotalMinutes > 1) //blok v verigi je ustrezen ce je njegova casovna znacka najvec 1 minuto manjsa od casovne znacke prejasnjeg bloka
                    return false;
            }

            return true;
        }

        //KUMULATIVNA TEZAVNOST
        private double CalculateCumulativeDifficulty(List<Block> blockchain)
        {
            double cumulativeDifficulty = 0;
            foreach (Block block in blockchain)
            {
                cumulativeDifficulty += Math.Pow(2, block.BlockDifficulty); //izracunamo kumulativno tezavnost, torej za vsak blok izracunamo 2^block_diff i sastejemo
            }
            return cumulativeDifficulty;
        }

        //Connect button - Starts Server thread
        private void btn_connect_Click(object sender, EventArgs e)
        {
            btn_connect.Enabled = false;
            btn_mine.Enabled = true;
            textBox_username.Enabled = false;
            textBox_port.Enabled = false;
            textBox_connect_to.Enabled = true;

            Thread ServerThread = new Thread(StartServer);
            ServerThread.IsBackground = true;
            ServerThread.Start();
        }

        //Mine button - Starts the mining process, new thread
        private void btn_mine_Click(object sender, EventArgs e)
        {
            btn_mine.Enabled = false;

            Thread MiningThread = new Thread(StartMining);
            MiningThread.IsBackground = true;
            MiningThread.Start();
        }

        //Connect Port button - Updates the list of connected ports based on input in port textbox
        private void btn_connect_to_Click(object sender, EventArgs e)
        {
            try
            {
                int port = Convert.ToInt32(textBox_connect_to.Text);

                if (ConnectedClientsList.Contains(port))
                    ConnectedClientsList.Remove(port);
                else
                    ConnectedClientsList.Add(port);

                UpdateConnectedPortsTextBox();
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
            finally
            {
                textBox_connect_to.Text = "";
            }
        }
        private void textBox_username_TextChanged(object sender, EventArgs e)
        {
            btn_connect.Enabled = !string.IsNullOrEmpty(textBox_username.Text) && !string.IsNullOrEmpty(textBox_port.Text);
        }

        private void textBox_port_TextChanged(object sender, EventArgs e)
        {
            btn_connect.Enabled = !string.IsNullOrEmpty(textBox_username.Text) && !string.IsNullOrEmpty(textBox_port.Text);
        }

        private void textBox_connect_to_TextChanged(object sender, EventArgs e)
        {
            btn_connect_to.Enabled = !string.IsNullOrEmpty(textBox_connect_to.Text);
        }

        //Starts server, listens on port from port textbox for new connections and starts a new thread with each client
        private void StartServer()
        {
            int Port = Convert.ToInt32(textBox_port.Text);
            TcpListener listener = new TcpListener(IPAddress.Parse(IP), Port);
            listener.Start();

            while (true)
            {
                try
                {
                    TcpClient tcpClient = listener.AcceptTcpClient();

                    Thread ListenerThread = new Thread(Communication);
                    ListenerThread.IsBackground = true;
                    ListenerThread.Start(tcpClient);
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Listener:\n" + ex.Message + "\n" + ex.StackTrace);
                }
            }
        }

        //Method for communicating with other clients
        private void Communication(object obj)
        {
            TcpClient tcpClient = (TcpClient)obj; //casts the received obj to client
            NetworkStream ns = tcpClient.GetStream(); //received the NetworkStream
            List<Block> newBlockChain = new List<Block>(); //new blockchain list stores the blocks received from the network
            string buffer = "";
            string message;

            do
            {
                buffer = buffer + Receive(ns);

                while (true)
                {
                    int index = buffer.IndexOf('\n');   //finds the index of \n. each block ends with a newline

                    if (index == -1)
                        break;

                    message = buffer.Substring(0, index); //message is extracted from 0 to index
                    buffer = buffer.Substring(index + 1); //bugger is reseted to start at the index + 1 position

                    newBlockChain = JsonConvert.DeserializeObject<List<Block>>(message); //converts json back to blockChain
                    ManageBlockChain(newBlockChain);
                }
            }
            while (ns.DataAvailable);

            ns.Close();
            tcpClient.Close();
        }

        private void UpdateConnectedPortsTextBox()
        {
            richTextBox_chain.Invoke(new Action(() =>
            {
                richTextBox_chain.Text = "";
                foreach (int port in ConnectedClientsList)
                {
                    richTextBox_chain.AppendText("Connected to: " + port.ToString() + "\n");
                }
            }));
        }
        private string Receive(NetworkStream ns)
        {
            try
            {
                byte[] myReadBuffer = new byte[1024];
                int len = ns.Read(myReadBuffer, 0, myReadBuffer.Length);
                string message = Encoding.Default.GetString(myReadBuffer, 0, len);
                return message;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Receive:\n" + ex.Message + "\n" + ex.StackTrace);
                return null;
            }
        }

        private void Send(NetworkStream ns, List<Block> chain)
        {
            try
            {
                string message = JsonConvert.SerializeObject(chain);
                message = message + "\n";
                byte[] myWriteBuffer = Encoding.Default.GetBytes(message);
                ns.Write(myWriteBuffer, 0, myWriteBuffer.Length);
            }
            catch (Exception ex)
            {
                MessageBox.Show("Send:\n" + ex.Message + "\n" + ex.StackTrace);
            }
        }
        private void Form1_Load(object sender, EventArgs e)
        {

        }
    }
}