from nltk.tokenize import RegexpTokenizer
import nltk
import json


from random import randint

endOfJoke = "<s>"
beginOfJoke = "</s>"
COUNT_THRESHOLD = 1

class Node:

    def __init__(self, wordCountPairs):
        # [List-of (Word, Nat)]
        # where each pair represents a word that follows
        # the word representing this node, and their frequencies.
    
        # TODO keep a list of accumulative frequencies, and 
        # a parallel list of the corresponding words
        if wordCountPairs == []:
            raise RuntimeError("Cannot have Node with empty list of following words")

        self.words = list(map(lambda pair : pair[0], wordCountPairs))
        self.frequencies = list(map(lambda pair : pair[1], wordCountPairs))
        self.totalFrequency = sum(self.frequencies)

    def randomSelect(self):
        # _ -> Word
        # randomly select a word that follows this node's word.
        # base on their frequencies.

        accumulatedFrequency = 0
        threshold = randint(1, self.totalFrequency)

        for i in range(len(self.words)):
            accumulatedFrequency += self.frequencies[i]
            if accumulatedFrequency >= threshold:
                return self.words[i]

        raise RuntimeError("Unreachable code: Node with strings" + str(self.words))

class Model:

    def __init__(self, word_to_lowc, pair_to_lowc):
        # [List [Map Word [List-of (Word Count])]  [Map (Word, Word) [List-of (Word Count])]
        # process the list of word-count pair into [Map Word Node]
        
        word_to_node = {}

        for word in word_to_lowc.keys():
            word_count_pairs = word_to_lowc[word]
            word_to_node[word] = Node(word_count_pairs)

        pair_to_node = {}
        for pair in pair_to_lowc.keys():
            pair_count_pairs = pair_to_lowc[pair]
            pair_to_node[pair] = Node(pair_count_pairs)

        self.word_to_node = word_to_node
        self.pair_to_node = pair_to_node

    def generate_text(self, initial, length):
        # take in an initial word to start the text, and
        # maximum length of the text, generate a new Text

        words = [initial]
        
        # invariant: i == len(words)
        for i in range(1, length):
            # generate next word with bi-gram map if possible,
            # else use uni-gram map, which is guaranteed to generate the next word
            last_word = words[-1]
            last_last_word = words[-2] if i > 1 else ""
            pair_key = last_last_word + " " + last_word
            
            node = self.pair_to_node[pair_key] if pair_key in self.pair_to_node else self.word_to_node[last_word]
            nextWord = node.randomSelect()
            if nextWord == endOfJoke:
                break
            words.append(nextWord)

        return " ".join(words)
    
    def _next_word_unigram(self, word):
        # generate the next word based on unigram model
        return self.word_to_node[word].randomSelect()



def tokenize_data_from_file(file_in, file_out):
    # The input file is stored in Json format in terms of
    # "body" -> String. the joke in 
    # "id" -> String. unique id for the joke
    # "score" -> Int. The score of this joke
    # "title" -> title of this joke

    # tokenized into {"tokens" : [List [List Token]], "scores" : [List Nat]}
    # they are guaranteed to be the same in size

    loj = []
    los = []

    with open(file_in, 'r') as data:
        import re
        p = re.compile(r"^(([a-zA-Z]+('[a-zA-Z])?)|[0-9]+)[,.?!]?$")
        for joke in json.load(data):
            lower_split = joke["body"].lower().split()
            # if no token is disqualified by regex
            if len(list(filter(p.match, lower_split))) == len(lower_split):
                loj.append(lower_split)
                los.append(joke["score"])


    with open(file_out, 'w') as out:
        json.dump({"tokens" : loj, "scores" : los}, out)




def process_data_from_file(file_in, file_out):

    # given data json format:
    # {"tokens" : [List [List Token]], "scores" : [List Nat]}
    # count/frequency is increased based on the score

    # NOTE beginOfJoke is added to the beginning of each joke twice
    # because we are using a tri-gram model

    # process the data from given file into 
    # ([Map Word [List (Word Count)]], [Map Pair-Word [List (Word Count)]])
    # where Word is a single word string
    # Pair-Word is Word + " " + Word
    # and save it to a file with given name in json format

    # list of jokes, which is a list of words
    loj = []
    # list of scores, parallel to loj
    los = []
    # keep a sentence with words and punctuation with only , . ! ?
    # tokenizer = RegexpTokenizer(r'[\w,!?.]+')

    with open(file_in, 'r') as data:
         token_and_scores = json.load(data)
         loj = token_and_scores["tokens"]
         los = token_and_scores["scores"]
         if len(loj) != len(los):
             raise RuntimeError("jokes and scores size not equal")



    def update_counter_map(counter_map, key_to_counter, word, delta):
        # [Map X [Map Word Nat]] X Word -> _
        # add the given word to the counter associated with the given key.
        # increase the counter by delta

        # Initialize counter if it does not exist
        if key_to_counter not in counter_map:
            counter_map[key_to_counter] = {}
        
        counter = counter_map[key_to_counter]

        # Initialize count if the word is not added yet
        if word not in counter:
            counter[word] = 0

        counter[word] += delta



    # map a word to [Map Word Nat]
    # where the counter represents the number of times a word appears after this word
    strToCounter = {}
    # map (word, word) to [Map Word Nat]
    # This is used by a bi-gram model
    pairToCounter = {}
    from math import log

    for index in range(len(loj)):
        last_word = beginOfJoke
        last_last_word = beginOfJoke

        padded_joke = [beginOfJoke, beginOfJoke]
        padded_joke.extend(loj[index])
        padded_joke.append(endOfJoke)
        # increment count based on log of score
        delta = log(los[index] + 1, 2)

        for i in range(2, len(padded_joke)):
            word = padded_joke[i]
            update_counter_map(strToCounter, last_word, word, delta)
            update_counter_map(pairToCounter, (last_last_word, last_word), word, delta)
            # udpate cached last words
            last_last_word = last_word
            last_word = word

    # convert data to [Map Word Node]
    word_to_node = {}

    for word in strToCounter.keys():
        counter = strToCounter[word]
        candidates = []

        for nextWord in counter.keys():
            # must convert the counter value to an int. since
            # it's sum of some log values
            count = int(counter[nextWord])
            if count > COUNT_THRESHOLD:
                candidates.append( (nextWord, count) )

        if candidates:
            word_to_node[word] = candidates


    # convert data to [Map (Word, Word) Node]
    # since json does not allow key to be tuple,
    # key will be "word word" with a space in between.
    pair_to_node = {}
 
    for pair in pairToCounter.keys():
        counter = pairToCounter[pair]
        candidates = []

        for nextWord in counter.keys():
            # must convert the counter value to an int. since
            # it's sum of some log values
            count = int(counter[nextWord])
            if count > COUNT_THRESHOLD:
                candidates.append( (nextWord, count) )

        if candidates:
            pair_str = pair[0] + " " + pair[1]
            pair_to_node[pair_str] = candidates
   
    with open(file_out, 'w') as output:
        str_to_map = {"unigram" : word_to_node, "bigram" : pair_to_node}
        json.dump(str_to_map, output)





def dump_data():
    config = {
      'user': 'scott',
      'password': 'password',
      'host': '127.0.0.1',
      'database': 'employees',
      'raise_on_warnings': True
    }    
    
    cnx = mysql.connector.connect(**config)

    with open("wocka-processed.json", "r") as data:
        pair = json.load(data)
        # [Map Word [List [List Word Nat]]]
        unigram = pair["unigram"]
        # [Map Word [List [List Word Nat]]]
        # Word here is Word + " " + Word
        bigram = pair["bigram"]

        for word in unigram.key():
            list_of_counter = unigram[word]
            for next_word, count in list_of_counter:
                # TODO add next_word, count into wherever word is 
                pass

        for pair in bigram.key():
            # endoced as Word + " " + Word
            string = pair.split()
            last_word = string[0]
            word = string[1]

            list_of_counter = bigram[string]
            for next_word, count in list_of_counter:
                # TODO add next_word, count into wherever word last_word is 
                pass


    cnx.close()

def main():
    model = None

    with open("wocka-processed.json", "r") as data:
        pair = json.load(data)
        model = Model(pair["unigram"], pair["bigram"])

    while True:
        print("Please give the initial word and maximum count")
        try:
            initial = input()
            count = int(input())
            print(model.generate_text(initial, count))
        except KeyError:
            pass
        except ValueError:
            pass

        
tokenize_data_from_file("./data/jokes-data.json", "data/jokes-data-tokenized.json")
process_data_from_file("./data/jokes-data-tokenized.json", "./data/jokes-data-processed.json")
# main()
# dump_data()

