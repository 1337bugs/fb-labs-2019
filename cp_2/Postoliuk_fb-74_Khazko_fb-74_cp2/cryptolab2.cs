using System;
using System.Collections.Generic;
using System.Text;  
using System.Console;
using System.GetString;

const string ru_alf = "абвгдежзийклмнопрстуфхцчшщъыьэюя";
const int ru_alf_l = 32;
static readonly double ru_sovp_indeks = 0.0553;
static readonly Dictionary<string, double> ru_b_chast;


namespace labvig_2
{
	class LinkedHashMap<T, U>
{
    Dictionary<T, LinkedListNode<Tuple<U, T>>> D = new Dictionary<T, LinkedListNode<Tuple<U, T>>>();
    LinkedList<Tuple<U,T>> LL = new LinkedList<Tuple<U, T>>();

    public U this[T c]
    {
        get
        {
            return D[c].Value.Item1;
        }

        set
        {
            if(D.ContainsKey(c))
            {
                LL.Remove(D[c]);
            }

            D[c] = new LinkedListNode<Tuple<U, T>>(Tuple.Create(value, c));
            LL.AddLast(D[c]);
        }
    }

    public bool ContainsKey(T k)
    {
        return D.ContainsKey(k);
    }

    public U PopFirst()
    {
        var node = LL.First;
        LL.Remove(node);
        D.Remove(node.Value.Item2);
        return node.Value.Item1;
    }

    public int Count
    {
        get
        {
            return D.Count;
        }
    }
}

	public class Vigenere
	{
		static vigener()
		{
			LinkedHashMap<string, double> map = new LinkedHashMap<string, double>();
            ru_b_chast = new LinkedHashMap<string, double>(Collections.unmodifiableMap(sortByValue(map)));
            //easiest way to put letters to map
			//
			//
			//
			map.put("\u043e", 0.10983);
			map.put("\u0435", 0.08483);
			map.put("\u0430", 0.07998);
			map.put("\u0438", 0.07367);
			map.put("\u043d", 0.067);
			map.put("\u0442", 0.06318);
			map.put("\u0441", 0.05473);
			map.put("\u0440", 0.04746);
			map.put("\u0432", 0.04533);
			map.put("\u043b", 0.04343);
			map.put("\u043a", 0.03486);
			map.put("\u043c", 0.03203);
			map.put("\u0434", 0.02977);
			map.put("\u043f", 0.02804);
			map.put("\u0443", 0.02615);
			map.put("\u044f", 0.02001);
			map.put("\u044b", 0.01898);
			map.put("\u044c", 0.01735);
			map.put("\u0433", 0.01687);
			map.put("\u0437", 0.01641);
			map.put("\u0431", 0.01592);
			map.put("\u0447", 0.0145);
			map.put("\u0439", 0.01208);
			map.put("\u0445", 0.00966);
			map.put("\u0436", 0.0094);
			map.put("\u0448", 0.00718);
			map.put("\u044e", 0.00639);
			map.put("\u0446", 0.00486);
			map.put("\u0449", 0.00361);
			map.put("\u044d", 0.00331);
			map.put("\u0444", 0.00267);
			map.put("\u044a", 3.7E-4);
		}

		private static double stat_sovp(string text, int r)
		{
			double unsafe stat = 0;
			int textLength = text.Length;

			for (int i = 0; i < textLength - r; i++)
			{
				if (text[i] == text[i + r]) {
					stat++;
				}
			}
			return stat;
		}


        private static double indSovp(string text)
		{
			text = text.ToLower().replaceAll(string.Format("[^{0}]", ru_alf), ""); //havent found the correct way
			IDictionary<int, long> collect = text.chars().boxed().collect(Collectors.groupingBy(x => x, Collectors.counting()));
			double textLength = text.Length;
			return collect.Values.Select(x => x * (x - 1)).Sum() / (textLength * (textLength - 1));
		}


                internal static class StringHelper
        {
            
            public static string SubstringSpecial(this string self, int start, int end)
            {
                return self.Substring(start, end - start);
            }
            public static bool StartsWith(this string self, string prefix, int toffset)
            {
                return self.indeksOf(prefix, toffset, System.StringComparison.Ordinal) == toffset;
            }
            public static string[] Split(this string self, string regexDelimiter, bool trimTrailingEmptyStrings)
            {
                string[] splitArray = System.Text.RegularExpressions.Regex.Split(self, regexDelimiter);

                if (trimTrailingEmptyStrings)
                {
                    if (splitArray.Length > 1)
                    {
                        for (int i = splitArray.Length; i > 0; i--)
                        {
                            if (splitArray[i - 1].Length > 0)
                            {
                                if (i < splitArray.Length)
                                    System.Array.Resize(ref splitArray, i);

                                break;
                            }
                        }
                    }
                }

                return splitArray;
            }
            public static string NewString(sbyte[] bytes)
            {
                return NewString(bytes, 0, bytes.Length);
            }
            public static string NewString(sbyte[] bytes, int indeks, int count)
            {
                return System.Text.Encoding.UTF8.GetString((byte[])(object)bytes, indeks, count);
            }
            public static string NewString(sbyte[] bytes, string encoding)
            {
                return NewString(bytes, 0, bytes.Length, encoding);
            }
            public static string NewString(sbyte[] bytes, int indeks, int count, string encoding)
            {
                return System.Text.Encoding.GetEncoding(encoding).GetString((byte[])(object)bytes, indeks, count);
            }
            public static sbyte[] GetBytes(this string self)
            {
                return GetSBytesForEncoding(System.Text.Encoding.UTF8, self);
            }
            public static sbyte[] GetBytes(this string self, System.Text.Encoding encoding)
            {
                return GetSBytesForEncoding(encoding, self);
            }
            public static sbyte[] GetBytes(this string self, string encoding)
            {
                return GetSBytesForEncoding(System.Text.Encoding.GetEncoding(encoding), self);
            }
            private static sbyte[] GetSBytesForEncoding(System.Text.Encoding encoding, string s)
            {
                sbyte[] sbytes = new sbyte[encoding.GetByteCount(s)];
                encoding.GetBytes(s, 0, s.Length, (byte[])(object)sbytes, 0);
                return sbytes;
            }
        }


        	private static bool SI_match(List<StringBuilder> parts)
		{
			return parts.Select(part => indSovp(part.ToString())).Any(ci => Math.Abs(ci - ru_sovp_indeks.Value) < 0.001D);
		}


		private static List<StringBuilder> parts(string text, int parts)
		{
			List<StringBuilder> parts = new List<StringBuilder>(parts);

			for (int i = 0; i < parts; i++)
			{
				parts.Add(new StringBuilder());
			}

			for (int i = 0, textLength = text.Length; i < textLength; i++)
			{
				parts[i % parts].Append(text[i]);
			}

			return parts;
		}
	}
private static List<int> probableKeys(string shT)
{
        LinkedHashMap<string, double> ch_b = sorted(map);
		List<int> probableKeys = new List<int>();
		Dictionary<string, double> map = monogramsFrequency(ru_alf, shT);
		string most_ch_b = ch_b.keySet().GetEnumerator().next();
		foreach (string c in ru_b_chast.keySet())
		{
			probableKeys.Add((most_ch_b[0] - c[0] + ru_alf_LENGTH) % ru_alf_LENGTH);
		}
		return probableKeys;
}

	private static bool text_test(string text)
	{
		List<string> text_ch_b = new List<string>(sorted.(monogramsFrequency(ru_alf, text)).keySet());
		Console.WriteLine(" check \n")
		List<string> ru_ch_b = new List<string>(sorted.(ru_b_chast).keySet());
		string dec_ch_b = string.join("", ruch_b.sub_list(0, 15));
        double ctr = 0;
        i=10
		while (i>0)
		{
			if (dec_ch_b.Contains(textch_b[i]))
			{
				ctr++;
                i--;
			}
		}
		return (ctr / 10) >= 0.9;
	}


	private static string encrypt(string pl_txt, string key)
	{
		char firstLetter = ru_alf[0];
		Supplier<char> keyCharsSupplier = new SupplierAnonymousInnerClass(key);
		return pl_txt.chars().map(c => (c + keyCharsSupplier.get() - 2*firstLetter)%ru_alf_LENGTH+firstLetter)
        .mapToObj(c=>(char)c).collect(Collector
        .of(StringBuilder::new, StringBuilder.append, StringBuilder.append, StringBuilder.toString));
	}

	private class SupplierAnonymousInnerClass : Supplier<>
	{
		private string key;
    public SupplierAnonymousInnerClass(string key)
		{
			this.key = key;
		}

		internal int j = 0;
        public char get()
		{   
            char keychar = 0;
            keyChar = key[j];
			j = (j + 1) % key.Length;
			return keyChar;
		}
	}

	private static string decrypt(string shT)
	{
        Console.WriteLine("stat. dlya ver. klyuchey:");
		shT = shT.ToLower();
		int dl_klyucha;

		foreach (dl_klyucha = 1; dl_klyucha <= 5; dl_klyucha++)
		{
			List<StringBuilder> parts = parts(shT, dl_klyucha);
			if (stat_sovp(parts))
			{
				break;
			}
		}
		for(;;)
		{
			Console.Write("%d . \n", dl_klyucha, stat_sovp(shT, dl_klyucha).intVal());
            Console.WriteLine(" " + endl);
			if (stat_sovp(shT, dl_klyucha) / stat_sovp(shT, dl_klyucha - 1) > 1.5D)
			{
				break;
			}
			dl_klyucha++;
		}
        Console.WriteLine(" " + endl);
		Console.WriteLine("dlina klyucha = " + dl_klyucha);
        Console.WriteLine(" " + endl);

		List<List<int>> probableKeysArray = new List<List<int>>();
		List<StringBuilder> parts = parts(shT, dl_klyucha);
		StringBuilder key = new StringBuilder();

		foreach (int j = 0; j < dl_klyucha; j++)
		{
			probableKeysArray.Add(probableKeys(parts[j].ToString()));
			key.Append(ru_alf.charAt(probableKeysArray[j][0]));
		}
        string pl_txt = decrypt(shT, key.ToString());
        Console.WriteLine(" " + endl);
		Console.WriteLine("shifr_t: " + shT.Substring(0, 50));
        Console.WriteLine(" " + endl);
		Console.WriteLine("raschifr_t: " + pl_txt.Substring(0, 50));\
        Console.WriteLine(" " + endl);
		Console.WriteLine("klyuch:  " + key);

		foreach (int i = 0, j = 0; i < dl_klyucha; i++, j = 0)
		{
			string part = decrypt(parts[i].ToString(), key.substring(i, 1));
			while (!text_test(part) && j < 32)
			{
				key[i] = ru_alf.charAt(probableKeysArray[i][j]);
				part = decrypt(parts[i].ToString(), key.substring(i, 1));
                Console.WriteLine(" " + endl);
				System.out.printf(string.Format("\n", 7 + i), "|");
				Console.WriteLine("Key:  " + key);
				j++;
			}
		}
pl_txt = decrypt(shT, key.ToString());
    Console.WriteLine(" " + endl);
	Console.WriteLine("raschifr: " + pl_txt.Substring(0, 50));
    return pl_txt;
	}

	private static string decrypt(string shT, string key)
	{
		StringBuilder pl_txt = new StringBuilder();
		shT = shT.ToLower();
		key = key.ToLower();
		int textLength = shT.Length;
		char firstLetter = ru_alf.charAt(0);

		for (int i = 0, j = 0; i < textLength; i++)
		{
			char c = shT[i];
			if (ru_alf.contains("" + c))
			{
				pl_txt.Append((char)((c - key[j] + ru_alf_LENGTH) % ru_alf_LENGTH + firstLetter));
				j++;
				j %= key.Length;
			}
		}
		return pl_txt.ToString();
	}
	public static void Main(string[] args)
	{
		string path = Directory.GetCurrentDirectory();
		string pl_txt = (new string(Files.readAllBytes(pathToFile))).ToLower().replaceAll(string.Format("[^{0}]", ru_alf), "");
		Console.WriteLine("ind. sovpad. for plain text        : " + indSovp(pl_txt));
		string encryptedText;
        Console.WriteLine(" " + endl);
		encryptedText = encrypt(pl_txt, "он");
		Console.WriteLine("ind. sovpad. dlya klyucha s dlinoy  2: " + indSovp(encryptedText));
		encryptedText = encrypt(pl_txt, "бог");
		Console.WriteLine("ind. sovpad. dlya klyucha s dlinoy  3: " + indSovp(encryptedText));
		encryptedText = encrypt(pl_txt, "царь");
		Console.WriteLine("ind. sovpad. dlya klyucha s dlinoy  4: " + indSovp(encryptedText));
		encryptedText = encrypt(pl_txt, "война");
		Console.WriteLine("ind. sovpad. dlya klyucha s dlinoy  5: " + indSovp(encryptedText));
		encryptedText = encrypt(pl_txt, "левниколаевичтолстой");
		Console.WriteLine("ind. sovpad. dlya klyucha s dlinoy 20: " + indSovp(encryptedText));
		Console.WriteLine(" " + endl);
        Console.WriteLine(" " + endl);
        Console.WriteLine(" " + endl);
        string shT = new string(Files.readAllBytes(Paths.get("resources", "sh_t.txt")));
		string decryptedText = decrypt(shT);
		Files.write(Paths.get("path", "pl_txt.txt"), decryptedText.GetBytes());
	}
}
