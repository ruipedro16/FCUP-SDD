#!/usr/bin/env runhaskell
import Control.Arrow ((&&&))
import Control.Monad (liftM2)
import Data.Bifunctor (second)
import Data.Char (toUpper)
import Data.Function (on)
import Data.List (nub, sortBy)

capitalise :: String -> String
capitalise = unwords . map (\(x:xs) -> toUpper x : xs) . words

acronyms :: [(String, String)]
acronyms =
    [ ("P2P", "Peer-To-Peer")
    , ("PoW", "Proof-of-Work")
    , ("PoS", "Proof-of-Stake")
    , ("DPoS", "Delegated Proof-of-Stake")
    , ("RPC", "remote procedure call")
    , ("UTXO", "Unspent Transaction Output")
    ]

main :: IO ()
main =
    writeFile "acros.tex" . concatMap ((\(a, s) -> concat ["\\acro{", a, "}{", s, "}\n"])) $
    sortBy (compare `on` (map toUpper . fst)) $ map (second capitalise) acronyms
